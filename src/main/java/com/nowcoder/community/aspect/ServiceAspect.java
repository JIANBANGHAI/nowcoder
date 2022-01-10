package com.nowcoder.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
@Component
@Aspect
public class ServiceAspect {
    public static  final Logger logger = LoggerFactory.getLogger(ServiceAspect.class);
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointCut(){

    }
    @Pointcut("execution(* com.nowcoder.community.service.LikeService.getLikeCount(..))")
    public void afterPointCut(){

    }

    @Pointcut("execution(* com.nowcoder.community.service.LikeService.getLike(..))")
    public void after1PointCut(){

    }


    @Before("pointCut()")
    public void before(JoinPoint joinPoint){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteHost();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        //获取访问类的路径
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        logger.info(String.format("用户[%s],在[%s],访问了[%s].", ip, time, target));
    }
    @AfterReturning(returning="rvt", pointcut = "afterPointCut()")
    public Object AfterExec(JoinPoint joinPoint,Object rvt){
        //pointcut是对应的注解类   rvt就是方法运行完之后要返回的值
        logger.info("获取目标方法的返回值：" + rvt);
        return rvt;
    }
    @AfterReturning(returning="rvt", pointcut = "after1PointCut()")
    public Object AfterExec1(JoinPoint joinPoint,Object rvt){
        //pointcut是对应的注解类   rvt就是方法运行完之后要返回的值
        logger.info("获取目标方法的返回值：" + rvt);
        return rvt;
    }

}
