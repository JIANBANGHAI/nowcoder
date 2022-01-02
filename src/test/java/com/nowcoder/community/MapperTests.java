package com.nowcoder.community;

import com.nowcoder.community.dao.*;
import com.nowcoder.community.entity.*;
import com.nowcoder.community.utils.ThreadUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    @Resource
    private UserMapper userMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Resource
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private ThreadUtil threadLocal;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void updateUser() {
        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150, "http://www.nowcoder.com/102.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150, "hello");
        System.out.println(rows);
    }

    @Test
    public void activeTest() {
        List<Discuss> listAll = discussPostMapper.getListAll(103, 1, 10);
        listAll.forEach(s -> System.out.println(s));
    }

    @Test
    public void activeTest1() {
        int listCount = discussPostMapper.getListCount(0);
        System.out.println(listCount);
    }

    @Test
    public void ticketTest() {
        int test = loginTicketMapper.insertLoginTicket(new LoginTicket(0, 1, "test", 1, new Date()));
        System.out.println(test);
    }
    @Test
    public void ticketTest1() {
        LoginTicket test = loginTicketMapper.selectByLoginTickek("test");
        System.out.println(test);
    }
    @Test
    public void insertDisCussTest1() {
        Discuss discuss = new Discuss();
        discuss.setUserId(171);
        discuss.setTitle("来了");
        discuss.setContent("abc");
        discuss.setCreateTime(new Date());
        discussPostMapper.insertDisPost(discuss);
    }

    @Test
    public void selectIdByDiscuss(){
        Discuss discuss = discussPostMapper.selectDiscussById(101);
        try {
            System.out.println(discuss);
        } catch (Exception e) {
            System.out.println("error");
        }
    }
    @Test
    public void commentMapperTest(){
        int commentComment = commentMapper.getCommentComment(2, 12);
        List<Comment> listComment = commentMapper.getListComment(2, 12, 1, 5);
        System.out.println(listComment.size());
        System.out.println(commentComment);
    }
    @Test
    public void addCommentTest(){
        Comment comment = new Comment();
        comment.setUserId(170);
        comment.setEntityType(1);
        comment.setContent("admin test");
        comment.setCreateTime(new Date());
        int i = commentMapper.addComment(comment);
        System.out.println(i);
    }

    @Test
    public void queryAllMessage(){
        List<Message> listAll = messageMapper.getListAll(111, 0, 20);
//        listAll.forEach(s-> System.out.println(s));
        List<Message> letters = messageMapper.getLetters("111_113", 0, 10);
//        letters.forEach(s-> System.out.println(s));
        int listCount = messageMapper.getListCount(111);
        int i = messageMapper.selectLettersCount("111_113");
        System.out.println("listcount:"+listCount+","+"letterscouont:"+i);
    }

}