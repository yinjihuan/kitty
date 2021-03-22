package com.cxytiandi.kitty.servicecall.feign;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.openfeign.FeignContext;

public class FeignContextBeanPostProcessor implements BeanPostProcessor {

    private final BeanFactory beanFactory;
    private MockFeignObjectWrapper traceFeignObjectWrapper;

    FeignContextBeanPostProcessor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        if (bean instanceof FeignContext && !(bean instanceof MockFeignContext)) {
            return new MockFeignContext(getTraceFeignObjectWrapper(), (FeignContext) bean);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

    private MockFeignObjectWrapper getTraceFeignObjectWrapper() {
        if (this.traceFeignObjectWrapper == null) {
            this.traceFeignObjectWrapper = this.beanFactory.getBean(MockFeignObjectWrapper.class);
        }
        return this.traceFeignObjectWrapper;
    }
}
