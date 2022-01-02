package com.nowcoder.community.service;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginStatus;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.MailClinet;
import com.nowcoder.community.utils.ThreadUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService  implements LoginStatus {
    @Value("${community.path.domin}")
    private String domin;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Resource
    private UserMapper userMapper;

    @Autowired
    private MailClinet mailClinet;

    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private ThreadUtil threadUtil;
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    public User getUserById(int id) {
        return  userMapper.selectById(id);
    }

    public Map<String,Object> registerResult(User user){
        Map<String,Object> map = new HashMap<>();
        if (user==null){
            throw new NullPointerException("user对象为Null");
        }
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","密码为空");
            return map;
        }

        User user1 = userMapper.selectByName(user.getUsername());
        if (user1!=null){
            map.put("usernameMsg","该账号存在");
            return map;
        }
        User user2 = userMapper.selectByEmail(user.getEmail());
        if (user2!=null){
            map.put("emailMsg","该邮箱存在");
            return map;
        }

        user.setSalt(CommunityUtil.generateUUId().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setActivationCode(CommunityUtil.generateUUId());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt()*1000));
        user.setCreateTime(new Date());

        userMapper.insertUser(user);

        Context context = new Context();
        context.setVariable("email",user.getEmail());
        //http://localhost:8080/community/activation/164/5735a50b8a574e0c91ff0633ef7aa166
        String url = domin+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);
        String process = templateEngine.process("/mail/activation", context);
        mailClinet.send(user.getEmail(),"激活邮件",process);
        //发送激活邮件
        return map;
    }

    public int checkEmail(int userId,String code){
        User user = userMapper.selectById(userId);
        if (user.getStatus()==1){
            return EXIST;
        }else if (user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            return SUCCESS;
        }else {
            return FIELD;
        }

    }

    public Map<String,Object> login( String username,String password,int expired){
        Map<String,Object> map = new HashMap<>();
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码为空");
            return map;
        }
        User user = userMapper.selectByName(username);

        //验证账号
        if(user==null){
            map.put("usernameMsg","该账号不存在");
            return map;
        }

        //验证状态
        if (user.getStatus()==0){
            map.put("usernameMsg","用户未激活");
            return map;
        }
        password = CommunityUtil.md5(password+user.getSalt());
        if (!user.getPassword().equals(password)){
            map.put("passwordMsg","密码不存在");
            return map;
        }

        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUId());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expired*1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket",loginTicket.getTicket());
        return map;
    }
    public void loginOut(String ticket ){
        loginTicketMapper.updateLoginTicket(ticket,1);
    }

    public LoginTicket getTicket(String ticket){
        return loginTicketMapper.selectByLoginTickek(ticket);
    }

    public Map<String,Object> updatePwd(String password ,String newPassword,String confirmPassword) {
        Map<String, Object> map = new HashMap<>();
        User user = threadUtil.getThreadLocal();
        String pwdFormTb = user.getPassword();
        if (password==null){
            map.put("pwdMsg","密码为空");
            return map;
        }

        if (newPassword==null){
            map.put("newPwdMsg","新密码不能为空");
            return map;
        }
        if (password.equals(newPassword)){
            map.put("pwdMsg","新密码不能和原密码一致");
            return map;
        }

        password = CommunityUtil.md5(password+user.getSalt());
        if (!password.equals(pwdFormTb)){
            map.put("pwdMsg","密码错误,该密码不存在");
            return map;
        }

        if(confirmPassword==null){
            map.put("confirmMsg","确认密码不能为空");
            return map;
        }

        if ( !confirmPassword.equals(newPassword)){
            map.put("confirmMsg","密码不一致");
            return map;
        }

        confirmPassword = CommunityUtil.md5(confirmPassword+user.getSalt());
        userMapper.updatePassword(user.getId(),confirmPassword);
        return map;
    }
    public int updateUrlHeader(int id, String headerUrl){
        return userMapper.updateHeader(id,headerUrl);
    }
}