package com.cxytiandi.kitty.lock.idempotent.aspect;

import com.cxytiandi.kitty.lock.idempotent.aspect.MethodWrapper;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 代码使用的是https://github.com/alibaba/Sentinel/tree/master/sentinel-extension/sentinel-annotation-aspectj
 * 中提供的，没必要重复造轮子
 */
final class ResourceMetadataRegistry {

    private static final Map<String, MethodWrapper> HANDLER_MAP = new ConcurrentHashMap<>();

    static MethodWrapper lookupHandler(Class<?> clazz, String name) {
        return HANDLER_MAP.get(getKey(clazz, name));
    }

    static void updateHandlerFor(Class<?> clazz, String name, Method method) {
        if (clazz == null || !StringUtils.hasText(name)) {
            throw new IllegalArgumentException("Bad argument");
        }
        HANDLER_MAP.put(getKey(clazz, name), MethodWrapper.wrap(method));
    }

    private static String getKey(Class<?> clazz, String name) {
        return String.format("%s:%s", clazz.getCanonicalName(), name);
    }

}
