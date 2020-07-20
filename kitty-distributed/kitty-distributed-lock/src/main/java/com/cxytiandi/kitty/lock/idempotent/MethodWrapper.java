package com.cxytiandi.kitty.lock.idempotent;

import java.lang.reflect.Method;

/**
 * 代码使用的是https://github.com/alibaba/Sentinel/tree/master/sentinel-extension/sentinel-annotation-aspectj
 * 中提供的，没必要重复造轮子
 */
public class MethodWrapper {

    private final Method method;
    private final boolean present;

    private MethodWrapper(Method method, boolean present) {
        this.method = method;
        this.present = present;
    }

    static MethodWrapper wrap(Method method) {
        if (method == null) {
            return none();
        }
        return new MethodWrapper(method, true);
    }

    static MethodWrapper none() {
        return new MethodWrapper(null, false);
    }

    Method getMethod() {
        return method;
    }

    boolean isPresent() {
        return present;
    }
}
