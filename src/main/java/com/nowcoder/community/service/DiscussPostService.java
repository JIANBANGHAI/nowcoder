package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.Discuss;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DiscussPostService {
    @Resource
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;
    public List<Discuss> getListAll(int userId,int start,int end){
        return discussPostMapper.getListAll(userId,start,end);
    }

    public int getListCount(int userId){
        return discussPostMapper.getListCount(userId);
    }

    public int insertDiscuss(Discuss discuss){
        if (discuss==null){
            throw new NullPointerException("discuss is not exists!");
        }
        if (discuss.getTitle()==null ){
            CommunityUtil.getFastJson(302,"标题不能为空");
        }
        if (discuss.getContent()==null){
            CommunityUtil.getFastJson(302,"正文不能为空");
        }
        //防止注入攻击
        discuss.setTitle(HtmlUtils.htmlEscape(discuss.getTitle()));
        discuss.setContent(HtmlUtils.htmlEscape(discuss.getContent()));

        //铭感词过滤
        discuss.setTitle(sensitiveFilter.filter(discuss.getTitle()));
        discuss.setContent(sensitiveFilter.filter(discuss.getContent()));
        int i = discussPostMapper.insertDisPost(discuss);

        return i!=0?i:0;
    }
    public Discuss getDiscuss(int id){
        return discussPostMapper.selectDiscussById(id);
    }

    public int updateCommentCount(int id,int commentCount){
        return discussPostMapper.updateDiscussCount(id,commentCount);
    }
}
