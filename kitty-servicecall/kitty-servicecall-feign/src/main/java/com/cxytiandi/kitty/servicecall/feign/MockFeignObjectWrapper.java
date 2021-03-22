package com.cxytiandi.kitty.servicecall.feign;


import com.cxytiandi.kitty.servicecall.feign.config.ApiMockProperties;
import feign.Client;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.openfeign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient;
import org.springframework.context.annotation.Configuration;

public class MockFeignObjectWrapper {
    private final BeanFactory beanFactory;

    private CachingSpringLoadBalancerFactory cachingSpringLoadBalancerFactory;
    private SpringClientFactory springClientFactory;

    private ApiMockProperties apiMockProperties;

    MockFeignObjectWrapper(BeanFactory beanFactory, ApiMockProperties apiMockProperties) {
        this.beanFactory = beanFactory;
        this.apiMockProperties = apiMockProperties;
    }

    Object wrap(Object bean) {
        if (bean instanceof Client && !(bean instanceof MockFeignContext)) {
            if (bean instanceof LoadBalancerFeignClient) {
                LoadBalancerFeignClient client = ((LoadBalancerFeignClient) bean);
                return new MockLoadBalancerFeignClient(
                        client.getDelegate(), factory(),
                        clientFactory(), apiMockProperties);
            }
        }
        return bean;
    }

    CachingSpringLoadBalancerFactory factory() {
        if (this.cachingSpringLoadBalancerFactory == null) {
            this.cachingSpringLoadBalancerFactory = this.beanFactory
                    .getBean(CachingSpringLoadBalancerFactory.class);
        }
        return this.cachingSpringLoadBalancerFactory;
    }

    SpringClientFactory clientFactory() {
        if (this.springClientFactory == null) {
            this.springClientFactory = this.beanFactory
                    .getBean(SpringClientFactory.class);
        }
        return this.springClientFactory;
    }

}
