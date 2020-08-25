package com.cxytiandi.kitty.lock.autoconfigure;

import com.cxytiandi.kitty.lock.idempotent.DistributedIdempotent;
import com.cxytiandi.kitty.lock.idempotent.aspect.DistributedIdempotentAspect;
import com.cxytiandi.kitty.lock.idempotent.DistributedIdempotentImpl;
import com.cxytiandi.kitty.lock.idempotent.properties.IdempotentProperties;
import com.cxytiandi.kitty.lock.idempotent.storage.IdempotentStorageFactory;
import com.cxytiandi.kitty.lock.idempotent.storage.IdempotentStorageMongo;
import com.cxytiandi.kitty.lock.idempotent.storage.IdempotentStorageMysql;
import com.cxytiandi.kitty.lock.idempotent.storage.IdempotentStorageRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-04-03 23:27
 */
@Configuration
@ImportAutoConfiguration(IdempotentProperties.class)
@AutoConfigureAfter(DistributedLockAutoConfiguration.class)
public class IdempotentAutoConfiguration {

    @Autowired
    private DistributedIdempotent distributedIdempotent;

    @Bean
    public DistributedIdempotent distributedIdempotent() {
        return new DistributedIdempotentImpl();
    }

    @Bean
    public DistributedIdempotentAspect distributedIdempotentAspect() {
        return new DistributedIdempotentAspect(distributedIdempotent);
    }

    @Bean
    public IdempotentStorageFactory idempotentStorageFactory() {
        return new IdempotentStorageFactory();
    }

    @ConditionalOnClass(MongoTemplate.class)
    @Configuration
    protected static class MongoTemplateConfiguration {
        @Bean
        public IdempotentStorageMongo idempotentStorageMongo() {
            return new IdempotentStorageMongo();
        }
    }


    @ConditionalOnClass(JdbcTemplate.class)
    @Configuration
    protected static class JdbcTemplateConfiguration {
        @Bean
        public IdempotentStorageMysql idempotentStorageMysql() {
            return new IdempotentStorageMysql();
        }
    }

    @Bean
    public IdempotentStorageRedis idempotentStorageRedis() {
        return new IdempotentStorageRedis();
    }
}