package com.aprado.transacaoApi.config;

import java.time.Clock;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

@Configuration 
public class ClockConfig {
    
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
