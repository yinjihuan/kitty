package com.cxytiandi.kitty.servicecall.feign;

import com.cxytiandi.kitty.servicecall.feign.config.ApiMockProperties;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ApiMockProperties.class)
public class FeignBeanPostProcessorConfiguration {

    @Bean
    public MockFeignObjectWrapper mockFeignObjectWrapper(BeanFactory beanFactory, ApiMockProperties apiMockProperties) {
        return new MockFeignObjectWrapper(beanFactory, apiMockProperties);
    }

    @Bean
    public FeignContextBeanPostProcessor feignContextBeanPostProcessor(BeanFactory beanFactory) {
        return new FeignContextBeanPostProcessor(beanFactory);
    }

}
