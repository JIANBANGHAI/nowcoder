package com.nowcoder.community.controller;

import com.nowcoder.community.annotition.LoginRequire;
import com.nowcoder.community.entity.LoginStatus;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.MyAttentionService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.ThreadUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;
import org.slf4j.Logger;

@Controller
@RequestMapping("/user")
public class UserController implements LoginStatus {
    @Value("${community.path.domin}")//用域名
    private String domin;
    @Value("${head.portrait.path}")//储存本地路径
    private String uploadPath;
    @Value("${server.servlet.context-path}")//当前访问路径
    private String contentPath;
    private static final Logger logger  = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private ThreadUtil threadUtil;
    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private MyAttentionService myAttentionService;

    @LoginRequire
    @RequestMapping(path = "/setting", method = RequestMethod.GET )
    public String showSetting(){
        return "/site/setting";
    }

    @LoginRequire
    @RequestMapping(method = RequestMethod.POST,value = "/updatePwd")
    public String updatePwd(Model model, String password, String newPassword,
                                 String confirmPassword){
        Map<String, Object> map = userService.updatePwd(password,newPassword,confirmPassword);
        if (map.isEmpty()){
            model.addAttribute("msg","修改密码成功，请重新登陆");
            model.addAttribute("target","/login");
            return "/site/operate-result";
        }else {
            model.addAttribute("pwdMsg",map.get("pwdMsg"));
            model.addAttribute("newPwdMsg",map.get("newPwdMsg"));
            model.addAttribute("confirmMsg",map.get("confirmMsg"));
            return "/site/setting";
        }
    }

    @RequestMapping(method = RequestMethod.POST,value = "/upload")
    public String upload(MultipartFile multipartFile,Model model){
        if (multipartFile==null ){
            model.addAttribute("error", "您还没有选择图片!");
            return "/site/setting";
        }

        /**自定义上传图片名，去重**/
        String filename = multipartFile.getOriginalFilename();//当前原文件名
        //取出当前索引后的所有字符，.png/.jpeg/
        String substring = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(substring)){
            model.addAttribute("error","文件格式不正确");
            return "/site/setting";
        }
        filename = CommunityUtil.generateUUId()+substring;//abdaf.png
        /**保存文件**/

        File desc = new File(uploadPath+"/"+filename);
        try {
            multipartFile.transferTo(desc);
        } catch (IOException e) {
            System.out.println("保存文件error");
        }
        /**修改文件路径**/
        User user = threadUtil.getThreadLocal();
        //http://localhost:8080/community/user/upload/abc.png
        String newUrl = domin+contentPath+"/user/upload/"+filename;
        userService.updateUrlHeader(user.getId(),newUrl);
        return "redirect:/index";
    }
    @RequestMapping(method = RequestMethod.GET,path = "/upload/{newFileName}")
    ////http://localhost:8080/community/user/D:/img1/upload/abc.png
    public void getPath(HttpServletResponse response, @PathVariable("newFileName")String newFileName){
        //使用response写出type
        newFileName= uploadPath+ "/"+newFileName;
        String suffix = newFileName.substring(newFileName.lastIndexOf("."));
        response.setContentType("image/"+suffix);

        //使用response写出图片
        try (
                OutputStream os = response.getOutputStream();
                FileInputStream fis = new FileInputStream(newFileName);
                ){
            byte[] bytes = new byte[1024];
            int num = 0;
            while ((num = fis.read(bytes))!=-1){
                os.write(bytes,0,num);
            }
        } catch (IOException e) {
            System.out.println("读取头像失败");
        }
    }
    @RequestMapping(method = RequestMethod.GET,path = "/profile/{entityUserId}")
    public String getHomeProfile(@PathVariable("entityUserId") int entityUserId,Model model){
        User user = userService.getUserById(entityUserId);
        if (user==null){
            throw new NullPointerException("用户为空!");
        }
        //用户
        model.addAttribute("user",user);

        //赞
        int userCount = likeService.getUserCount(entityUserId);
        model.addAttribute("userCount",userCount);

        //关注..人
        long followCount = myAttentionService.getFollowCount(entityUserId, ENTITY_USER_DISCUSS);
        model.addAttribute("followCount",followCount);

        //关注者
        long followerCount = myAttentionService.getFollowerCount(ENTITY_USER_DISCUSS, entityUserId);
        model.addAttribute("followerCount",followerCount);

        //关注状态
        boolean b = false;
        if (threadUtil.getThreadLocal() != null) {
            b = myAttentionService.followerStatus(threadUtil.getThreadLocal().getId(), ENTITY_USER_DISCUSS, user.getId());
        }
        logger.debug("b:"+b);
        model.addAttribute("b", b);
        return "/site/profile";
    }
}
