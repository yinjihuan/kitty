package com.cxytiandi.kitty.db.shardingjdbc.aspect;

import com.cxytiandi.kitty.db.shardingjdbc.annotation.MasterRoute;
import org.apache.shardingsphere.api.hint.HintManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * 路由设置切面
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-10-12 21:12
 */
@Aspect
public class MasterRouteAspect {

    @Around("@annotation(masterRoute)")
    public Object aroundGetConnection(final ProceedingJoinPoint pjp, MasterRoute masterRoute) throws Throwable {
        HintManager hintManager = HintManager.getInstance();
        hintManager.setMasterRouteOnly();
        try {
            return pjp.proceed();
        } finally {
            hintManager.close();
        }
    }

}
