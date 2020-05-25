package com.cxytiandi.kitty.threadpool.autoconfigure;

import com.cxytiandi.kitty.threadpool.DynamicThreadPoolManager;
import com.cxytiandi.kitty.threadpool.DynamicThreadPoolProperties;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ImportAutoConfiguration(DynamicThreadPoolProperties.class)
@Configuration
public class DynamicThreadPoolAutoConfiguration {

    @Bean
    public DynamicThreadPoolManager dynamicThreadPoolManager() {
        return new DynamicThreadPoolManager();
    }

}