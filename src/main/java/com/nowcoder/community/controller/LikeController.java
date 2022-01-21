package com.nowcoder.community.controller;

import com.nowcoder.community.annotition.LoginRequire;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.Discuss;
import com.nowcoder.community.entity.LoginStatus;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.event.Event;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.ThreadUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.slf4j.Logger;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements LoginStatus {
    private static Logger logger = LoggerFactory.getLogger(LikeController.class);
    @Autowired
    private LikeService likeService;
    @Autowired
    private ThreadUtil threadUtil;
    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(method = RequestMethod.POST,path = "/like")
    @ResponseBody
    public String giveTheThumbsUp(int entityType,int entityId, int entityUserId,int postId){
        User user = threadUtil.getThreadLocal();
        likeService.getLike(user.getId(),entityType,entityId,entityUserId);

        long likeCount = likeService.getLikeCount(entityType, entityId);
        int likeStatus = likeService.getLikeStatus(user.getId(), entityType, entityId);
        Map<String,Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);

        if (likeStatus==1){
            Event event = new Event();
            event.setTopic(TOPIC_TYPE_LIKE);
            event.setUserId(user.getId());
            event.setEntityType(entityType);
            event.setEntityUserId(entityUserId);
            event.setData("postId",postId);
            event.setEntityId(entityId);
            eventProducer.send(event);
        }
        return CommunityUtil.getFastJson(200,null,map);
    }
}
