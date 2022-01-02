package com.nowcoder.community.service;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private DiscussPostService discussPostService;
    public List<Comment> getAllComment(int entityType,int entityId,int offset,int limit){
        return commentMapper.getListComment(entityType,entityId,offset,limit);
    }
    public int getCommentCount(int entityType,int entityId ){
        return commentMapper.getCommentComment(entityType,entityId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        if (comment==null){
            throw new NullPointerException("comment is not exists!");
        }
        if (comment.getContent()==null ){
            CommunityUtil.getFastJson(302,"内容不能为空");
        }
        //防止注入攻击
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        //铭感词过滤
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int i = commentMapper.addComment(comment);

        if (comment.getEntityType()==1){
            int commentComment = commentMapper.getCommentComment(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(),commentComment);
        }
        return i;
    }
}
