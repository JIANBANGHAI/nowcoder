package com.nowcoder.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.LoginStatus;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.ThreadUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
@Controller
public class MessageController implements LoginStatus {
    private static Logger logger = LoggerFactory.getLogger(MessageController.class);
    @Autowired
    private MessageService messageService;
    @Autowired
    private ThreadUtil threadUtil;
    @Autowired
    private UserService userService;



    @RequestMapping(method = RequestMethod.GET,path = "/letter/list")
    public String showLetter(Model model, Page page){
        User user = threadUtil.getThreadLocal();
        page.setLimit(5);
        page.setRows(messageService.getConversationCount(user.getId()));
        page.setPath("/letter/list");

        List<Message> conversationList = messageService.getConversationList(user.getId(), page.startPage(), page.getLimit());
        List<Map<String,Object>> conversations = new ArrayList<>();

        if (conversationList!=null){
            for (Message message : conversationList) {
                Map<String,Object> map = new HashMap<>();
                map.put("message",message);
                int targetId = user.getId()==message.getFromId()?message.getToId():message.getFromId();
                map.put("target",userService.getUserById(targetId));
                map.put("letterCount",messageService.getLettersCount(message.getConversationId()));
                map.put("unReadCount",messageService.getLettersUnReadCount(message.getConversationId(),user.getId()));
                conversations.add(map);
            }
        }
        model.addAttribute("conversations",conversations);
        model.addAttribute("letterUnReadCount",messageService.getLettersUnReadCount(null,user.getId()));;
        model.addAttribute("noticeUnReadCount",messageService.getMessageNoticeCountUnRead(user.getId(),null));
        return "/site/letter";
    }
    
    @RequestMapping(method = RequestMethod.GET,path = "/letter/list/{conversationId}")
    public String showDetail(@PathVariable("conversationId") String conversationId,Model model, Page page){
        User user = threadUtil.getThreadLocal();
        page.setLimit(5);
        page.setRows(messageService.getLettersCount(conversationId));
        page.setPath("/letter/list/"+conversationId);

        List<Map<String,Object>> letters = new ArrayList<>();
        List<Message> lettersList = messageService.getLettersList(conversationId, page.startPage(), page.getLimit());

        if (lettersList!=null){
            for (Message message : lettersList) {
                Map<String,Object> map = new HashMap<>();
                map.put("message",message);

                letters.add(map);
            }
        }
        model.addAttribute("user",getTargetUserName(conversationId));
        model.addAttribute("letters",letters);
        List<Integer> ids = getLetterIds(lettersList);

        //消息已读
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }
        return "/site/letter-detail";
    }

    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();

        if (letterList != null) {
            for (Message message : letterList) {
                if (threadUtil.getThreadLocal().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }

        return ids;
    }

    public User getTargetUserName(String conversationId){
        String[] s = conversationId.split("_");
        int id1 = Integer.parseInt(s[0]);
        int id2 = Integer.parseInt(s[1]);
        if (threadUtil.getThreadLocal().getId()==id1){
            return userService.getUserById(id2);
        }else {
            return userService.getUserById(id1);
        }
    }

    @GetMapping("/notice/list")
    public String getNotice(Model model){
        User threadLocal = threadUtil.getThreadLocal();
        Message message = messageService.getMessageByTopic(threadLocal.getId(), TOPIC_TYPE_COMMENT);

        //comment系统通知列表
        Map<String,Object> map = new HashMap<>();
        if (message!=null){
            map.put("message",message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
//            String content = JSONObject.toJSONString(message.getContent().toString());
            Map<String,Object> data = JSONObject.parseObject(content,HashMap.class);
            map.put("entityType",data.get("entityType"));
            map.put("entityId",data.get("entityId"));
            map.put("postId",data.get("postId"));
            map.put("user",userService.getUserById((Integer) data.get("userId")));

            int messageNoticeCount = messageService.getMessageNoticeCount(threadLocal.getId(), TOPIC_TYPE_COMMENT);
            map.put("messageNoticeCount",messageNoticeCount);

            int messageNoticeCountUnRead = messageService.getMessageNoticeCountUnRead(threadLocal.getId(), TOPIC_TYPE_COMMENT);
            map.put("messageNoticeCountUnRead",messageNoticeCountUnRead);
        }
        model.addAttribute("commentNotice",map);

        //点赞系统列表
        message = messageService.getMessageByTopic(threadLocal.getId(), TOPIC_TYPE_LIKE);
        map = new HashMap<>();
        if (message!=null){
            map.put("message",message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
//            String content = JSONObject.toJSONString(message.getContent().toString());
            Map<String,Object> data = JSONObject.parseObject(content,HashMap.class);
            map.put("entityType",data.get("entityType"));
            map.put("entityId",data.get("entityId"));
            map.put("postId",data.get("postId"));
            map.put("user",userService.getUserById((Integer) data.get("userId")));

            int messageNoticeCount = messageService.getMessageNoticeCount(threadLocal.getId(), TOPIC_TYPE_LIKE);
            map.put("messageNoticeCount",messageNoticeCount);

            int messageNoticeCountUnRead = messageService.getMessageNoticeCountUnRead(threadLocal.getId(), TOPIC_TYPE_LIKE);
            map.put("messageNoticeCountUnRead",messageNoticeCountUnRead);
        }
        model.addAttribute("likeNotice",map);

        // 查询关注类通知
        message = messageService.getMessageByTopic(threadLocal.getId(), TOPIC_TYPE_FOLLOW);
        map = new HashMap<>();
        if (message != null) {
            map.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            map.put("user", userService.getUserById((Integer) data.get("userId")));
            map.put("entityType", data.get("entityType"));
            map.put("entityId", data.get("entityId"));

            int count = messageService.getMessageNoticeCount(threadLocal.getId(), TOPIC_TYPE_FOLLOW);
            map.put("messageNoticeCount", count);

            int unread = messageService.getMessageNoticeCountUnRead(threadLocal.getId(), TOPIC_TYPE_FOLLOW);
            map.put("messageNoticeCountUnRead", unread);
        }
        model.addAttribute("followNotice", map);
        //头部数量显示
        model.addAttribute("letterUnReadCount",messageService.getLettersUnReadCount(null,threadLocal.getId()));
        model.addAttribute("noticeUnReadCount",messageService.getMessageNoticeCountUnRead(threadLocal.getId(),null));
        return "/site/notice";
    }

    @GetMapping("/notice/detail/{topic}")
    public String getDetail(@PathVariable("topic")String topic,Model model,Page page){
        User user = threadUtil.getThreadLocal();
        if (StringUtils.isNoneBlank(topic)){
            page.setLimit(5);
            page.setPath("/notice/detail/"+topic);
            page.setRows(messageService.getMessageNoticeCount(user.getId(),topic));

            List<Map<String,Object>> detailList = new ArrayList<>();
            List<Message> detailMessage = messageService.getDetailMessage(user.getId(), topic, page.startPage(), page.getLimit());
            if (detailMessage!=null){
                for (Message message : detailMessage) {
                    Map<String,Object> map = new HashMap<>();
                    map.put("message",message);
                    String content = HtmlUtils.htmlUnescape(message.getContent());
                    Map<String,Object> data = JSONObject.parseObject(content,HashMap.class);
                    map.put("entityType",data.get("entityType"));
                    map.put("entityId",data.get("entityId"));
                    map.put("user",userService.getUserById((Integer) data.get("userId")));
                    map.put("postId",data.get("postId"));
                    map.put("fromUser",userService.getUserById(message.getFromId()));
                    detailList.add(map);
                }
            }
            model.addAttribute("detailList",detailList);
            List<Integer> ids = getLetterIds(detailMessage);
            if (!ids.isEmpty()) {
                messageService.readMessage(ids);
            }
        }
        return "/site/notice-detail";
    }
}
