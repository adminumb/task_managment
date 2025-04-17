package com.example.logexecution.aspect;

import com.example.logexecution.annotation.LogExecutionTime;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogExecutionTimeAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogExecutionTimeAspect.class);

    @Around("@annotation(main.java.com.example.logexecution.annotation.LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        Object result = joinPoint.proceed();
        
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LogExecutionTime annotation = signature.getMethod().getAnnotation(LogExecutionTime.class);
        
        String methodName = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
        String customMessage = annotation.value();
        
        if (!customMessage.isEmpty()) {
            logger.info("{} - Execution time: {} ms", customMessage, executionTime);
        } else {
            logger.info("Method {} executed in {} ms", methodName, executionTime);
        }

        return result;
    }
}
