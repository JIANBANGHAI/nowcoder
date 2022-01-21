package com.nowcoder.community.interceptor;

import com.nowcoder.community.annotition.LoginRequire;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.utils.ThreadUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.slf4j.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MessageCountInterceptor implements HandlerInterceptor {
    private static Logger logger = LoggerFactory.getLogger(MessageCountInterceptor.class);
    @Autowired
    private MessageService messageService;
    @Autowired
    private ThreadUtil threadUtil;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = threadUtil.getThreadLocal();
        if (user!=null&& modelAndView!=null) {
            int lettersUnReadCount = messageService.getLettersUnReadCount(null, user.getId());
            int noticeCount = messageService.getMessageNoticeCountUnRead(user.getId(), null);
            logger.debug("messageCount:"+lettersUnReadCount+noticeCount);
            System.out.println("messageCount:"+lettersUnReadCount+noticeCount);
            modelAndView.addObject("messageCount", lettersUnReadCount + noticeCount);
        }
    }
}
