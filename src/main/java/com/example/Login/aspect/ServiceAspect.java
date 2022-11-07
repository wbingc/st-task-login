package com.example.Login.aspect;

import com.example.Login.entity.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceAspect {

    final Logger LOGGER = LogManager.getLogger();

    @Pointcut("execution(public String com.example.Login.services.LoginService.login(..)) && args(user)")
    public void getLoginPointcut(UserDTO user) {}

    @Before("getLoginPointcut(user) ")
    public void before(JoinPoint joinPoint, UserDTO user) {
        LOGGER.info("Before " + joinPoint.getSignature().getName() + " : " + user.getEmail() + " logging in.");
    }

    @AfterReturning("getLoginPointcut(user) ")
    public void after(JoinPoint joinPoint, UserDTO user) {
        LOGGER.info("After " + joinPoint.getSignature().getName() + " : " + user.getEmail() + " logged in.");
    }

    @AfterThrowing("getLoginPointcut(user) ")
    public void afterThrowing(JoinPoint joinPoint, UserDTO user) {
        LOGGER.error("Unable to compute hash of password at " + joinPoint.getSignature().getName());
    }


}
