package com.cxytiandi.kitty.lock;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 分布式锁接口
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-03-31 22:53
 */
public interface DistributedLock {

    /**
     * 加锁
     * @param key 锁Key
     * @param waitTime 尝试加锁，等待时间 (ms)
     * @param leaseTime 上锁后的失效时间 (ms)
     * @param success 锁成功执行的逻辑
     * @param fail 锁失败执行的逻辑
     * @return
     */
    <T> T lock(String key, int waitTime, int leaseTime, Supplier<T> success, Supplier<T> fail);

    /**
     * 加锁, 加锁失败立即返回
     * @param key 锁Key
     * @param leaseTime 上锁后的失效时间 (ms)
     * @param success 锁成功执行的逻辑
     * @param fail 锁失败执行的逻辑
     * @return
     */
    <T> T lock(String key, int leaseTime, Supplier<T> success, Supplier<T> fail);

    /**
     * 加锁, 加锁失败立即返回
     * @param key 锁Key
     * @param leaseTime 上锁后的失效时间
     * @param timeUnit 时间单位
     * @param success 锁成功执行的逻辑
     * @param fail 锁失败执行的逻辑
     * @return
     */
    <T> T lock(String key, int leaseTime, TimeUnit timeUnit, Supplier<T> success, Supplier<T> fail);

}
