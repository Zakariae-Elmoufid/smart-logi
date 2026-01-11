package com.example.SmartLogi.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* com.example.SmartLogi.services.*.*(..))")
    public void serviceMethods() {}

    @Before("serviceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        log.info(">>> Entering method: {} with args = {}",
                joinPoint.getSignature(), joinPoint.getArgs());
    }

    @After("serviceMethods()")
    public void logAfter(JoinPoint joinPoint) {
        log.info("<<< Exiting method: {}", joinPoint.getSignature());
    }

    @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Exception ex) {
        log.error("XXX Exception in method: {} with message: {}",
                joinPoint.getSignature(), ex.getMessage());
    }
}
