package com.cxytiandi.kitty.lock;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.WriteRedisConnectionException;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redis 分布式锁
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-03-31 22:53
 */
@Slf4j
public class DistributedLockRedis implements DistributedLock {

    private RedissonClient redissonClient;

    private DistributedLockMysql distributedLockMysql;

    public  DistributedLockRedis(RedissonClient redissonClient, DistributedLockMysql distributedLockMysql) {
        this.redissonClient = redissonClient;
        this.distributedLockMysql = distributedLockMysql;
    }

    @Override
    public <T> T lock(String key, int waitTime, int leaseTime, Supplier<T> success, Supplier<T> fail) {
        return doLock(key, waitTime, leaseTime, TimeUnit.MILLISECONDS, success, fail);
    }

    @Override
    public <T> T lock(String key, int leaseTime, Supplier<T> success, Supplier<T> fail) {
        return doLock(key, 0, leaseTime, TimeUnit.MILLISECONDS, success, fail);
    }

    @Override
    public <T> T lock(String key, int leaseTime, TimeUnit timeUnit, Supplier<T> success, Supplier<T> fail) {
        return doLock(key, 0, leaseTime, timeUnit, success, fail);
    }

    private <T> T doLock(String key, int waitTime, int leaseTime, TimeUnit timeUnit, Supplier<T> success, Supplier<T> fail) {
        try {
            RLock lock = null;
            try {
                lock = redissonClient.getLock(key);
            } catch (Exception e) {
                log.error("get Redis Lock Error", e);
                // 降级为数据库锁
                if (distributedLockMysql != null) {
                    return distributedLockMysql.lock(key, waitTime, leaseTime, success, fail);
                }
                return fail.get();
            }

            boolean tryLock = false;
            try {
                tryLock = lock.tryLock(waitTime, leaseTime, timeUnit);
            } catch (WriteRedisConnectionException e) {
                // 写入失败，降级为数据库锁
                if (distributedLockMysql != null) {
                    return distributedLockMysql.lock(key, waitTime, leaseTime, success, fail);
                }
            }

            if (!tryLock) {
                return fail.get();
            }

            try {
                return success.get();
            } catch (Exception e){
                throw e;
            } finally {
                if (lock.getHoldCount() != 0) {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}