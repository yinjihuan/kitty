package com.cxytiandi.kitty.lock.idempotent.storage;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-08-17 22:32
 */
@Slf4j
public class IdempotentStorageRedis implements IdempotentStorage {

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public IdempotentStorageTypeEnum type() {
        return IdempotentStorageTypeEnum.REDIS;
    }

    @Override
    public void setValue(String key, String value, long expireTime, TimeUnit timeUnit) {
        log.debug("Redis Set key:{}, Value:{}, expireTime:{}, timeUnit:{}", key, value, expireTime, timeUnit);
        RBucket<String> bucket = redissonClient.getBucket(key);
        if (bucket != null) {
            bucket.set(value, expireTime, timeUnit);
        }
    }

    @Override
    public String getValue(String key) {
        RBucket<String> bucket = redissonClient.getBucket(key);
        String value = bucket.get();
        log.debug("Redis Get key:{}, Value:{}", key, value);
        return value;
    }
}
