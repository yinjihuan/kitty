package com.cxytiandi.kitty.threadpool.autoconfigure;

import com.cxytiandi.kitty.threadpool.DynamicThreadPoolAlarm;
import com.cxytiandi.kitty.threadpool.DynamicThreadPoolManager;
import com.cxytiandi.kitty.threadpool.config.DynamicThreadPoolProperties;
import com.cxytiandi.kitty.threadpool.endpoint.ThreadPoolEndpoint;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ImportAutoConfiguration(DynamicThreadPoolProperties.class)
@Configuration
public class DynamicThreadPoolAutoConfiguration {

    @Bean
    public DynamicThreadPoolManager dynamicThreadPoolManager() {
        return new DynamicThreadPoolManager();
    }

    @Bean
    public ThreadPoolEndpoint threadPoolEndpoint() {
        return new ThreadPoolEndpoint();
    }

    @Bean
    @ConditionalOnProperty(value = "kitty.threadpools.enabled", matchIfMissing = true)
    public DynamicThreadPoolAlarm dynamicThreadPoolAlarm() {
        return new DynamicThreadPoolAlarm();
    }

}