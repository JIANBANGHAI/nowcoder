package com.nowcoder.community.controller;
import com.nowcoder.community.annotition.LoginRequire;
import com.nowcoder.community.entity.*;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.ThreadUtil;
import com.sun.javafx.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements LoginStatus {
    public static final Logger logger = new Logger();

    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private ThreadUtil threadUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;

    @LoginRequire
    @RequestMapping(method = RequestMethod.POST,path = "/addDiscuss")
    @ResponseBody
    public String addDiscuss(String title,String content){
        User threadLocal = threadUtil.getThreadLocal();
        if (threadLocal==null){
            CommunityUtil.getFastJson(403,"用户不也存在!");
        }
        Discuss discuss = new Discuss();
        discuss.setUserId(threadLocal.getId());
        discuss.setTitle(title);
        discuss.setContent(content);
        discuss.setCreateTime(new Date());
        discussPostService.insertDiscuss(discuss);
        //统一异常处理
        Map<String,Object> map = new HashMap<>();
        map.put("discuss",discuss);
        return CommunityUtil.getFastJson(200,"发布成功!",map);
    }

    @RequestMapping(method = RequestMethod.GET,path = "/detail/{detailId}")
    public String getDiscussById(@PathVariable("detailId") int detailId,Model model,Page page){
        //帖子对象
        Discuss discuss = discussPostService.getDiscuss(detailId);
        model.addAttribute("discuss",discuss);
        //用户对象
        User user = userService.getUserById(discuss.getUserId());
        model.addAttribute("user",user);
        //帖子数量
        long likeCount = likeService.getLikeCount(ENTITY_TYPE_DISCUSS, discuss.getId());
        model.addAttribute("likeCount",likeCount);
        //点赞状态
        int likeStatus = likeService.getLikeStatus(threadUtil.getThreadLocal().getId() == 0 ? 1 : threadUtil.getThreadLocal().getId(), ENTITY_TYPE_DISCUSS, detailId);
        model.addAttribute("likeStatus",likeStatus);

        page.setLimit(5);
        page.setPath("/discuss/detail/" + detailId);
        page.setRows(discuss.getCommentCount());
        //评论
        List<Comment> allComment = commentService.getAllComment(ENTITY_TYPE_DISCUSS, discuss.getId(),page.startPage(), page.getLimit());

        List<Map<String,Object>> listComment = new ArrayList<>();
        if (allComment!=null){
            for (Comment comment : allComment) {
                Map<String,Object> map = new HashMap<>();
                map.put("comment",comment);
                map.put("user",userService.getUserById(comment.getUserId()));
                //帖子数量
                likeCount = likeService.getLikeCount(ENTITY_ID_DISCUSS, comment.getId());
                map.put("likeCount",likeCount);
                //点赞状态
                likeStatus = likeService.getLikeStatus(threadUtil.getThreadLocal().getId() == 0 ? 1 : threadUtil.getThreadLocal().getId(), ENTITY_ID_DISCUSS, comment.getId());
                map.put("likeStatus",likeStatus);
                //回复
                List<Comment> allReply = commentService.getAllComment(ENTITY_ID_DISCUSS, comment.getId(), 0, Integer.MAX_VALUE);//显示最多回复

                List<Map<String,Object>> replyList = new ArrayList<>();
                if (allReply!=null){
                    for (Comment reply : allReply) {
                        Map<String,Object> mapForReply = new HashMap<>();
                        mapForReply.put("reply",reply);
                        mapForReply.put("user",userService.getUserById(reply.getUserId()));

                        User target = reply.getTargetId() == 0 ? null : userService.getUserById(reply.getTargetId());
                        mapForReply.put("target",target);

                        likeCount = likeService.getLikeCount(ENTITY_ID_DISCUSS, reply.getId());
                        mapForReply.put("likeCount",likeCount);
                        //点赞状态
                        likeStatus = likeService.getLikeStatus(threadUtil.getThreadLocal().getId() == 0 ? 1 : threadUtil.getThreadLocal().getId(), ENTITY_ID_DISCUSS, reply.getId());
                        mapForReply.put("likeStatus",likeStatus);

                        replyList.add(mapForReply);
                    }
                }
                map.put("replys",replyList);

                int replyCount = commentService.getCommentCount(ENTITY_ID_DISCUSS, comment.getId());
                map.put("replyCount",replyCount);

                listComment.add(map);
            }
        }
        model.addAttribute("comments",listComment);
        //回复
        return "/site/discuss-detail";
    }
}
