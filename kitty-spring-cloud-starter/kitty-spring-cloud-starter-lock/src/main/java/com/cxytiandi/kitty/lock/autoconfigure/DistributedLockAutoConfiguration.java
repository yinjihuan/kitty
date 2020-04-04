package com.cxytiandi.kitty.lock.autoconfigure;

import com.cxytiandi.kitty.lock.DistributedLock;
import com.cxytiandi.kitty.lock.DistributedLockMysql;
import com.cxytiandi.kitty.lock.DistributedLockRedis;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 分布式锁自动配置
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-04-01 22:31
 */
@Configuration
public class DistributedLockAutoConfiguration {

    @Autowired(required = false)
    private DataSource dataSource;

    @Bean("distributedLockMysql")
    public DistributedLock distributedLockMysql() {
        return new DistributedLockMysql(dataSource);
    }

    @Bean("distributedLockRedis")
    @Primary
    @DependsOn("distributedLockMysql")
    public DistributedLock distributedLockRedis(RedissonClient redissonClient, DistributedLockMysql distributedLockMysql) {
        return new DistributedLockRedis(redissonClient, distributedLockMysql);
    }


}