package com.cxytiandi.kitty.threadpool.autoconfigure;

import com.cxytiandi.kitty.threadpool.alarm.DynamicThreadPoolAlarm;
import com.cxytiandi.kitty.threadpool.DynamicThreadPoolManager;
import com.cxytiandi.kitty.threadpool.config.DynamicThreadPoolProperties;
import com.cxytiandi.kitty.threadpool.endpoint.ThreadPoolEndpoint;
import com.cxytiandi.kitty.threadpool.listener.ApolloConfigUpdateListener;
import com.cxytiandi.kitty.threadpool.listener.NacosCloudConfigUpdateListener;
import com.cxytiandi.kitty.threadpool.listener.NacosConfigUpdateListener;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
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
    @ConditionalOnProperty(value = "kitty.threadpools.alarm.enabled", matchIfMissing = true)
    public DynamicThreadPoolAlarm dynamicThreadPoolAlarm() {
        return new DynamicThreadPoolAlarm();
    }

    @Configuration
    @ConditionalOnClass(value = com.alibaba.cloud.nacos.NacosConfigProperties.class)
    protected static class NacosCloudConfiguration {

        @Bean
        public NacosCloudConfigUpdateListener nacosCloudConfigUpdateListener() {
            return new NacosCloudConfigUpdateListener();
        }

    }

    @Configuration
    @ConditionalOnClass(value = com.alibaba.nacos.api.config.ConfigService.class)
    @ConditionalOnMissingClass(value = { "com.alibaba.cloud.nacos.NacosConfigProperties" })
    protected static class NacosConfiguration {

        @Bean
        public NacosConfigUpdateListener nacosConfigUpdateListener() {
            return new NacosConfigUpdateListener();
        }

    }

    @Configuration
    @ConditionalOnClass(value = com.ctrip.framework.apollo.ConfigService.class)
    protected static class ApolloConfiguration {

        @Bean
        public ApolloConfigUpdateListener apolloConfigUpdateListener() {
            return new ApolloConfigUpdateListener();
        }

    }

}