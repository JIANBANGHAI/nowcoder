package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.entity.*;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import com.sun.deploy.net.HttpResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class LoginController implements LoginStatus {
//    private static final Logger logger = (Logger) LoggerFactory.getLogger(LoginController.class);
    @Value("${server.servlet.context-path}")
    private String contentPath;
    @Autowired
    private UserService userService;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private Producer kaptcharPoducer;
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @RequestMapping(method = RequestMethod.GET,path ="/index")
    public String getListAll(Model model, Page page){
        page.setPath("/index");
        page.setRows(discussPostService.getListCount(0));
        //显示当前的起始行并且显示上限
        List<Discuss> listAll = discussPostService.getListAll(0, page.startPage(), page.getLimit());
        List<Map<String,Object>> lists = new ArrayList<>();
        if (listAll.size() != 0) {
            for (Discuss discuss : listAll) {
                Map<String, Object> map = new HashMap();
                map.put("discuss",discuss);
                User user = userService.getUserById(discuss.getUserId());
                map.put("user",user);
                lists.add(map);
            }
        }
        model.addAttribute("lists",lists);
        return "/index";
    }

    @RequestMapping(method = RequestMethod.GET,value = "/register")
    public String GetRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(method = RequestMethod.POST,value = "/register")
    public String RegisterResult(Model model, User user){
        Map<String, Object> map = userService.registerResult(user);
        if (map.isEmpty()|| map==null){
            model.addAttribute("msg","我们已经发送了一封邮件到您的账户,请注意查收");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return  "/site/register";
        }
    }
    ////http://localhost:8080/community/101/code

    @RequestMapping(method = RequestMethod.GET,path = "/activation/{userId}/{code}")
    public String activeation(@PathVariable("userId") int userId,@PathVariable("code") String code,Model model){
        int i = userService.checkEmail(userId, code);
        if (i==SUCCESS){
            model.addAttribute("msg","激活成功");
            model.addAttribute("target","/login");
        }else if (i==EXIST){
            model.addAttribute("msg","重复激活");
            model.addAttribute("target","/index");
        }else {
            model.addAttribute("msg","激活失败");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }

    @RequestMapping(method = RequestMethod.GET,value = "/login")
    public String loginGet(){
        return "/site/login";
    }

    @RequestMapping(method = RequestMethod.GET,value = "/kaptcha")
    public void kaptcha(HttpServletResponse response, HttpSession httpSession){
        String text = kaptcharPoducer.createText();
        BufferedImage image = kaptcharPoducer.createImage(text);

        httpSession.setAttribute("kaptcha",text);
        response.setContentType("image/png");

        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(method = RequestMethod.POST,value = "/login")
    public String loginPost(String username,String password,String code,boolean remeberme,
                        Model model,HttpSession httpSession,HttpServletResponse response){
        //验证码
        String kaptcha = (String) httpSession.getAttribute("kaptcha");
        if(StringUtils.isBlank(code)||StringUtils.isBlank(kaptcha)||!kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码错误");
            return "/site/login";
        }
        //持续时间
        int remebermeTime = remeberme?DEFUALT_TIME:REMEBERME_TIME;

        //登陆校验
        Map<String, Object> login = userService.login(username, password, remebermeTime);
        if (login.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",login.get("ticket").toString());
            cookie.setPath(contentPath);
            cookie.setMaxAge(remebermeTime);
            response.addCookie(cookie);
            return "redirect:/index";
        }else {
            model.addAttribute("usernameMsg",login.get("usernameMsg"));
            model.addAttribute("passwordMsg",login.get("passwordMsg"));
            return "/site/login";
        }
    }
    
    @RequestMapping(method = RequestMethod.GET,value = "/loginout")
    public String logOut(HttpSession session,@CookieValue("ticket")String ticket){
        session.removeAttribute("kaptcha");
        userService.loginOut(ticket);
        return "redirect:index";
    }
}
