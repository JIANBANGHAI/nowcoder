package com.nowcoder.community.service;

import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<Message> getConversationList(int userId,int offset,int limit){
        return messageMapper.getListAll(userId,offset,limit);
    }

    public int getConversationCount(int userId){
        return messageMapper.getListCount(userId);
    }

    public List<Message> getLettersList(String conversationId,int offset,int limit){
        return messageMapper.getLetters(conversationId,offset,limit);
    }

    public int getLettersCount(String conversationId){
        return messageMapper.selectLettersCount(conversationId);
    }

    public int getLettersUnReadCount(String conversationId,int userId){
        return messageMapper.selectLettersUnReadCount(conversationId,userId);
    }

    public int insertStatus(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    public Message getMessageByTopic(int userId,String topic){
        return messageMapper.findMessageByNotice(userId,topic);
    }

    public int getMessageNoticeCount(int userId,String topic){
        return messageMapper.findMessageByNoticeCount(userId,topic);
    }

    public int getMessageNoticeCountUnRead(int userId,String topic){
        return messageMapper.findMessageByNoticeUnRead(userId,topic);
    }

    public int readMessage(List<Integer> ids) {
        return messageMapper.updateStatus(ids, 1);
    }

    public List<Message> getDetailMessage(int userId,String topic,int start,int end){
        List<Message> allDetailMessage = messageMapper.findAllDetailMessage(userId, topic, start, end);
        if (allDetailMessage!=null){
            return allDetailMessage;
        }
        throw new NullPointerException();
    }
}
