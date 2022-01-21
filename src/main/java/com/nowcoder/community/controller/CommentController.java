package com.nowcoder.community.controller;

import com.nowcoder.community.annotition.LoginRequire;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.Discuss;
import com.nowcoder.community.entity.LoginStatus;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.event.Event;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.ThreadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements LoginStatus {
    @Autowired
    private CommentService commentService;
    @Autowired
    private ThreadUtil threadUtil;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private EventProducer eventProducer;

    @LoginRequire
    @RequestMapping(method = RequestMethod.POST,path = "/add/{detailId}")
    public String addComment(@PathVariable("detailId")String detailId, Comment comment){
        User threadLocal = threadUtil.getThreadLocal();
        comment.setCreateTime(new Date());
        comment.setUserId(threadLocal.getId());
        comment.setStatus(0);
        commentService.addComment(comment);

        Event event = new Event();
        event.setTopic(TOPIC_TYPE_COMMENT);
        event.setUserId(threadLocal.getId());
        event.setEntityType(comment.getEntityType());
        event.setData("postId",detailId);

        if (comment.getEntityType()==ENTITY_TYPE_DISCUSS){
            Discuss discuss = discussPostService.getDiscuss(comment.getEntityId());
            event.setEntityId(discuss.getUserId());
        }else if(comment.getEntityType()==ENTITY_ID_DISCUSS){
            Comment commentById = commentService.getCommentById(comment.getEntityId());
            event.setEntityId(commentById.getUserId());
        }
        eventProducer.send(event);
        return "redirect:/discuss/detail/" + detailId;
    }
}
