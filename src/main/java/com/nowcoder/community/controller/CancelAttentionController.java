package com.nowcoder.community.controller;

import com.nowcoder.community.annotition.LoginRequire;
import com.nowcoder.community.entity.LoginStatus;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.event.Event;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.MyAttentionService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.ThreadUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

import org.slf4j.Logger;
@Controller
public class CancelAttentionController  implements LoginStatus{
    private static final Logger logger = LoggerFactory.getLogger(CancelAttentionController.class);
    @Autowired
    private ThreadUtil threadUtil;
    @Autowired
    private MyAttentionService myAttentionService;
    @Autowired
    private UserService userService;
    @Autowired
    private EventProducer eventProducer;

    @LoginRequire
    @RequestMapping(method = RequestMethod.POST,path = "/follow")
    @ResponseBody
    public String attention(int entityType,int entityId){
        User user = threadUtil.getThreadLocal();
        myAttentionService.follow(user.getId(),entityType,entityId);

        Event event = new Event();
        event.setTopic(TOPIC_TYPE_FOLLOW);
        event.setUserId(user.getId());
        event.setEntityType(entityType);
        event.setEntityUserId(entityId);
        event.setEntityId(entityId);
        eventProducer.send(event);
        return CommunityUtil.getFastJson(200,"已关注");
    }


    @RequestMapping(method = RequestMethod.POST,path = "/follower")
    @ResponseBody
    public String cancelAttention(int entityType,int entityId){
        User user = threadUtil.getThreadLocal();
        myAttentionService.onFollow(user.getId(),entityType,entityId);
        return CommunityUtil.getFastJson(200,"取关");
    }

    @RequestMapping(method = RequestMethod.GET,path = "/followPage/{userId}")
    public String showFollowPage(@PathVariable("userId")int userId, Page page, Model model){
        User user = userService.getUserById(userId);
        if (user==null){
            return null;
        }
        page.setLimit(5);
        page.setRows((int) myAttentionService.getFollowCount(userId,ENTITY_USER_DISCUSS));
        page.setPath("/followPage/"+userId);

        List<Map<String,Object>> mapList = myAttentionService.getFollowUserList(userId,page.startPage(),page.getLimit());
        model.addAttribute("user",user);
        if (mapList!=null){
            for (Map<String, Object> map : mapList) {
                User user1 = (User) map.get("user");
                map.put("hasRoleKey",getStatus(user1.getId()));
                logger.debug("hasRoleKey1:",getStatus(user1.getId()));
            }
        }
        model.addAttribute("user1",mapList);
        return "/site/followee";
    }

    @RequestMapping(method = RequestMethod.GET,path = "/followerPage/{userId}")
    public String showFollowerPage(@PathVariable("userId")int userId, Page page, Model model){
        User user = userService.getUserById(userId);
        if (user==null){
            return null;
        }
        page.setLimit(5);
        page.setRows((int) myAttentionService.getFollowerCount(ENTITY_USER_DISCUSS,userId));
        page.setPath("/followerPage/"+userId);

        List<Map<String,Object>> mapList = myAttentionService.getFollowerUserList(userId,page.startPage(),page.getLimit());
        model.addAttribute("user",user);
        if (mapList!=null){
            for (Map<String, Object> map : mapList) {
                User user1 = (User) map.get("user");
                map.put("hasRoleKey",getStatus(user1.getId()));
            }
        }
        model.addAttribute("user2",mapList);
        for (Map<String, Object> map : mapList) {
            Object user1 = map.get("user");
            Object followerTime = map.get("followerTime");
            System.out.println("user1："+user1);
            System.out.println("followerTime:"+followerTime);
        }
        return "/site/follower";
    }

    //模板需要的状态
    public boolean getStatus(int userId){
        if (threadUtil.getThreadLocal()==null){
        return false;
        }
        return myAttentionService.followerStatus(threadUtil.getThreadLocal().getId(), ENTITY_USER_DISCUSS, userId);
    }
}
