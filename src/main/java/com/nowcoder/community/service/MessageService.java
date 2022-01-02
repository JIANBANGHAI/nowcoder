package com.nowcoder.community.service;

import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;
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
}
