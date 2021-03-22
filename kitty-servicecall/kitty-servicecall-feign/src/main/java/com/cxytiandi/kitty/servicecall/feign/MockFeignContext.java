package com.cxytiandi.kitty.servicecall.feign;


import org.springframework.cloud.openfeign.FeignContext;

import java.util.HashMap;
import java.util.Map;

public class MockFeignContext extends FeignContext {

    private final MockFeignObjectWrapper mockFeignObjectWrapper;

    private final FeignContext delegate;

    MockFeignContext(MockFeignObjectWrapper mockFeignObjectWrapper,
                      FeignContext delegate) {
        this.mockFeignObjectWrapper = mockFeignObjectWrapper;
        this.delegate = delegate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getInstance(String name, Class<T> type) {
        T object = this.delegate.getInstance(name, type);
        return (T) this.mockFeignObjectWrapper.wrap(object);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getInstances(String name, Class<T> type) {
        Map<String, T> instances = this.delegate.getInstances(name, type);
        if (instances == null) {
            return null;
        }
        Map<String, T> convertedInstances = new HashMap<>();
        for (Map.Entry<String, T> entry : instances.entrySet()) {
            convertedInstances.put(entry.getKey(),
                    (T) this.mockFeignObjectWrapper.wrap(entry.getValue()));
        }
        return convertedInstances;
    }
}
