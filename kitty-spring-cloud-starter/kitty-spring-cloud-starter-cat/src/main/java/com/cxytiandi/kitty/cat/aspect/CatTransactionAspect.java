package com.cxytiandi.kitty.cat.aspect;

import com.cxytiandi.kitty.cat.annotation.CatTransaction;
import com.cxytiandi.kitty.common.cat.CatConstantsExt;
import com.cxytiandi.kitty.common.cat.CatTransactionManager;
import com.cxytiandi.kitty.common.json.JsonUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * CatTransaction 切面
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-04-26 21:28
 */
@Aspect
public class CatTransactionAspect {

    @Around("@annotation(catTransaction)")
    public Object aroundAdvice(ProceedingJoinPoint joinpoint, CatTransaction catTransaction) throws Throwable {
        String type = catTransaction.type();
        if (StringUtils.isEmpty(type)){
            type = CatConstantsExt.METHOD;
        }
        String name = catTransaction.name();
        if (StringUtils.isEmpty(name)){
            name = joinpoint.getSignature().getDeclaringType().getSimpleName() + "." + joinpoint.getSignature().getName();
        }

        Map<String, Object> data = new HashMap<>(1);
        if (catTransaction.isSaveParamToCat()) {
            Object[] args = joinpoint.getArgs();
            if (args != null) {
                data.put("params", JsonUtils.toJson(args));
            }
        }

        return CatTransactionManager.newTransaction(() -> {
            try {
                return joinpoint.proceed();
            } catch (Throwable throwable) {
               throw new RuntimeException(throwable);
            }
        }, type, name, data);
    }


}
