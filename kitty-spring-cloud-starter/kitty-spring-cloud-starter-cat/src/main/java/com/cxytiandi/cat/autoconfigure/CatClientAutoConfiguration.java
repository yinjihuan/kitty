package com.cxytiandi.cat.autoconfigure;

import com.cxytiandi.cat.CatClientInit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CatClientAutoConfiguration {

    @Value("${spring.application.name:unknown}")
    private String domain;

    @Value("${cat.servers:}")
    private String servers;

    @Bean
    public CatClientInit catClientInit() {
        return new CatClientInit(domain, servers);
    }

}