package com.cxytiandi.kitty.db.redis.spring;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-04-27 21:57
 */
@Aspect
public class RedisAspect {

    @Pointcut("target(org.springframework.data.redis.connection.RedisConnectionFactory)")
    public void connectionFactory() {}

    @Pointcut("execution(org.springframework.data.redis.connection.RedisConnection *.getConnection(..))")
    public void getConnection() {}

    @Pointcut("execution(org.springframework.data.redis.connection.RedisClusterConnection *.getClusterConnection(..))")
    public void getClusterConnection() {}

    @Around("getConnection() && connectionFactory()")
    public Object aroundGetConnection(final ProceedingJoinPoint pjp) throws Throwable {
        RedisConnection connection = (RedisConnection) pjp.proceed();
        return new CatMonitorRedisConnection(connection);
    }

    @Around("getClusterConnection() && connectionFactory()")
    public Object aroundGetClusterConnection(final ProceedingJoinPoint pjp) throws Throwable {
        RedisClusterConnection clusterConnection = (RedisClusterConnection) pjp.proceed();
        return new CatMonitorRedisClusterConnection(clusterConnection);
    }

}