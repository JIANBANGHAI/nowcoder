package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.ThreadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MessageController {
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
        model.addAttribute("letterUnReadCount",messageService.getLettersUnReadCount(null,user.getId()));
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
        return "/site/letter-detail";
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
}
