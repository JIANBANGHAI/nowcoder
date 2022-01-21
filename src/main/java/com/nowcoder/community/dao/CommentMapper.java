package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.Discuss;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentMapper {
    List<Comment> getListComment(int entityType,int entityId,int offset,int limit);

    int getCommentComment(int entityType,int entityId);

    int addComment(Comment comment);

    Comment findByIdComment(int id);
}
