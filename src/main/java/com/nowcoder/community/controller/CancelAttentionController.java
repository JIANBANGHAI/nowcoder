package com.nowcoder.community.controller;

import com.nowcoder.community.annotition.LoginRequire;
import com.nowcoder.community.entity.LoginStatus;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MyAttentionService;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.ThreadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CancelAttentionController  {
    @Autowired
    private ThreadUtil threadUtil;
    @Autowired
    private MyAttentionService myAttentionService;

    @LoginRequire
    @RequestMapping(method = RequestMethod.POST,path = "/follow")
    @ResponseBody
    public String attention(int entityType,int entityId){
        User user = threadUtil.getThreadLocal();
        myAttentionService.follow(user.getId(),entityType,entityId);
        return CommunityUtil.getFastJson(200,"已关注");
    }

    @LoginRequire
    @RequestMapping(method = RequestMethod.POST,path = "/follower")
    @ResponseBody
    public String cancelAttention(int entityType,int entityId){
        User user = threadUtil.getThreadLocal();
        myAttentionService.onFollow(user.getId(),entityType,entityId);
        return CommunityUtil.getFastJson(200,"取关");
    }

}
