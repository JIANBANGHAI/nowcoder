package com.nowcoder.community.controller;

import com.nowcoder.community.annotition.LoginRequire;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
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
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private ThreadUtil threadUtil;

    @LoginRequire
    @RequestMapping(method = RequestMethod.POST,path = "/add/{detailId}")
    public String addComment(@PathVariable("detailId")String detailId, Comment comment){
        User threadLocal = threadUtil.getThreadLocal();
        comment.setCreateTime(new Date());
        comment.setUserId(threadLocal.getId());
        comment.setStatus(0);
        commentService.addComment(comment);
        return "redirect:/discuss/detail/" + detailId;
    }
}
