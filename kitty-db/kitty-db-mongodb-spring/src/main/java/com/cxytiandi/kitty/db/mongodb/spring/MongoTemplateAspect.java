package com.cxytiandi.kitty.db.mongodb.spring;

import com.cxytiandi.kitty.common.cat.CatTransactionManager;
import com.cxytiandi.kitty.common.json.JsonUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.HashMap;
import java.util.Map;

/**
 * MongoTemplate监控
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-02-16 19:31
 */
@Aspect
public class MongoTemplateAspect {

    @Pointcut("execution(* org.springframework.data.mongodb.core.MongoTemplate.*(..))")
    public void pointcut() {}

    @Around("pointcut()")
    public Object around(final ProceedingJoinPoint pjp) throws Throwable {
        String callMethod = pjp.getSignature().getDeclaringType().getSimpleName() + "." + pjp.getSignature().getName();
        Map<String, Object> data = new HashMap<>();
        data.put("params", JsonUtils.toJson(pjp.getArgs()));

        return CatTransactionManager.newTransaction(() -> {
            try {
                return pjp.proceed();
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }, "Mongo", callMethod, data);
    }

}