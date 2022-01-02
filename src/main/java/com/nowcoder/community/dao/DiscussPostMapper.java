package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Discuss;
import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussPostMapper {
    List<Discuss> getListAll(int userId,int start,int end);

    int getListCount(@Param("userId") int userId);

    int insertDisPost(Discuss discuss);

    Discuss selectDiscussById(int id);

    int updateDiscussCount(int id,int commentCount);
}