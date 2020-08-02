package com.cxytiandi.kitty.lock.idempotent;

import com.cxytiandi.kitty.common.context.ContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

@Aspect
public class DistributedIdempotentAspect extends AbstractIdempotentAspectSupport {

    private DistributedIdempotent distributedIdempotent;

    public DistributedIdempotentAspect(DistributedIdempotent distributedIdempotent) {
        this.distributedIdempotent = distributedIdempotent;
    }

    @Around(value = "@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinpoint, Idempotent idempotent) throws Throwable {
        Object[] args = joinpoint.getArgs();
        Method method = ((MethodSignature) joinpoint.getSignature()).getMethod();

        String key = "";
        if (StringUtils.hasText(idempotent.spelKey())) {
            key = parseKey(idempotent.spelKey(), method, args);
        } else {
            key = ContextHolder.getCurrentContext().get("globalIdempotentId");
        }

        String userInputKey = idempotent.value();
        if (!StringUtils.hasText(userInputKey)) {
            userInputKey = method.getName();
        }
        String idempotentKey = userInputKey + ":" + key;

        IdempotentRequest request = IdempotentRequest.builder().key(idempotentKey)
                .firstLevelExpireTime(idempotent.firstLevelExpireTime())
                .secondLevelExpireTime(idempotent.secondLevelExpireTime())
                .timeUnit(idempotent.timeUnit())
                .lockExpireTime(idempotent.lockExpireTime())
                .build();

        try {
            return distributedIdempotent.execute(request, () -> {
                try {
                    return joinpoint.proceed();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }, () -> {
                throw new IdempotentException("重复请求");
            });
        } catch (IdempotentException ex) {
            return handleIdempotentException(joinpoint, idempotent, ex);
        }
    }

    /**
     * 获取幂等的key, 支持SPEL表达式
     * @param key
     * @param method
     * @param args
     * @return
     */
    private String parseKey(String key, Method method, Object[] args){
        LocalVariableTableParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
        String[] paraNameArr = nameDiscoverer.getParameterNames(method);

        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        for(int i = 0;i < paraNameArr.length; i++){
            context.setVariable(paraNameArr[i], args[i]);
        }
        try {
            return parser.parseExpression(key).getValue(context, String.class);
        } catch (SpelEvaluationException e) {
            throw new RuntimeException("SPEL表达式解析错误", e);
        }
    }

}
