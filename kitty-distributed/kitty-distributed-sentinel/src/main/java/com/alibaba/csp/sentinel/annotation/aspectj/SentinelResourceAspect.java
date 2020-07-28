/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.annotation.aspectj;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.cxytiandi.kitty.common.cat.CatTransactionManager;
import com.cxytiandi.kitty.common.json.JsonUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Aspect for methods with {@link SentinelResource} annotation.
 *
 * @author Eric Zhao
 */
@Aspect
public class SentinelResourceAspect extends AbstractSentinelAspectSupport {

    @Pointcut("@annotation(com.alibaba.csp.sentinel.annotation.SentinelResource)")
    public void sentinelResourceAnnotationPointcut() {
    }

    @Around("sentinelResourceAnnotationPointcut()")
    public Object invokeResourceWithSentinel(ProceedingJoinPoint pjp) throws Throwable {
        Method originMethod = resolveMethod(pjp);

        SentinelResource annotation = originMethod.getAnnotation(SentinelResource.class);
        if (annotation == null) {
            // Should not go through here.
            throw new IllegalStateException("Wrong state for SentinelResource annotation");
        }
        String resourceName = getResourceName(annotation.value(), originMethod);
        EntryType entryType = annotation.entryType();
        int resourceType = annotation.resourceType();
        Map<String, Object> catData = new HashMap<>(1);
        catData.put("entryType", entryType.name());
        catData.put("resourceType", resourceType);
        catData.put("blockHandler", annotation.blockHandler());
        catData.put("blockHandlerClass", JsonUtils.toJson(annotation.blockHandlerClass()));
        catData.put("fallback", annotation.fallback());
        catData.put("fallbackClass", JsonUtils.toJson(annotation.fallbackClass()));
        return CatTransactionManager.newTransaction(() -> {
            try {
                return doInvoke(pjp, annotation, resourceName, entryType, resourceType);
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }, "Sentinel", resourceName, catData);
    }

    private Object doInvoke(ProceedingJoinPoint pjp, SentinelResource annotation, String resourceName, EntryType entryType, int resourceType) throws Throwable {

        Entry entry = null;
        try {
            entry = SphU.entry(resourceName, resourceType, entryType, pjp.getArgs());
            Object result = pjp.proceed();
            return result;
        } catch (BlockException ex) {
            return CatTransactionManager.newTransaction(() -> {
                try {
                    return handleBlockException(pjp, annotation, ex);
                } catch (Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
            }, "Sentinel", "handleBlockException");

        } catch (Throwable ex) {
            Class<? extends Throwable>[] exceptionsToIgnore = annotation.exceptionsToIgnore();
            // The ignore list will be checked first.
            if (exceptionsToIgnore.length > 0 && exceptionBelongsTo(ex, exceptionsToIgnore)) {
                throw ex;
            }
            if (exceptionBelongsTo(ex, annotation.exceptionsToTrace())) {
                traceException(ex, annotation);
                return CatTransactionManager.newTransaction(() -> {
                    try {
                        return handleFallback(pjp, annotation, ex);
                    } catch (Throwable throwable) {
                        throw new RuntimeException(throwable);
                    }
                }, "Sentinel", "handleFallback");

            }

            // No fallback function can handle the exception, so throw it out.
            throw ex;
        } finally {
            if (entry != null) {
                entry.exit(1, pjp.getArgs());
            }
        }
    }
}
