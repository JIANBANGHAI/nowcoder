package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageMapper {

    //查询当前会话列表，针对每个会话显示最新1条数据
    List<Message> getListAll(int userId, int offset, int limit);

    //当前会话列表会话数量
    int getListCount( int userId);

    //查询某个会话包括私信列表
    List<Message> getLetters(String conversationId, int offset, int limit);

    //每个会话列表私信数量显示
    int selectLettersCount(String conversationId);

    //未读私信数量

    int selectLettersUnReadCount(String conversationId,int userId);


}
