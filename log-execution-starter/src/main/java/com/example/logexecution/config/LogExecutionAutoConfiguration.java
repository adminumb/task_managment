package com.example.logexecution.config;

import com.example.logexecution.aspect.LogExecutionTimeAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class LogExecutionAutoConfiguration {

    @Bean
    public LogExecutionTimeAspect logExecutionTimeAspectM() {
        return new LogExecutionTimeAspect();
    }

}
