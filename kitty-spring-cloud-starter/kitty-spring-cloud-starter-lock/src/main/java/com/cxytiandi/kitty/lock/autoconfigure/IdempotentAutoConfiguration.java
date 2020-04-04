package com.cxytiandi.kitty.lock.autoconfigure;

import com.cxytiandi.kitty.lock.DistributedLock;
import com.cxytiandi.kitty.lock.idempotent.DistributedIdempotent;
import com.cxytiandi.kitty.lock.idempotent.DistributedIdempotentAspect;
import com.cxytiandi.kitty.lock.idempotent.DistributedIdempotentImpl;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-04-03 23:27
 */
@Configuration
@AutoConfigureAfter(DistributedLockAutoConfiguration.class)
public class IdempotentAutoConfiguration {

    @Autowired
    private DistributedIdempotent distributedIdempotent;

    @Bean
    public DistributedIdempotent distributedIdempotent(RedissonClient redissonClient, DistributedLock distributedLock) {
        return new DistributedIdempotentImpl(redissonClient, distributedLock);
    }

    @Bean
    public DistributedIdempotentAspect distributedIdempotentAspect() {
        return new DistributedIdempotentAspect(distributedIdempotent);
    }

}