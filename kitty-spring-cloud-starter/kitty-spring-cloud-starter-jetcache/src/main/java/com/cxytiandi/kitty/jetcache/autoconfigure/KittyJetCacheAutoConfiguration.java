package com.cxytiandi.kitty.jetcache.autoconfigure;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CreateCache;
import com.alicp.jetcache.autoconfigure.JedisPoolFactory;
import com.alicp.jetcache.autoconfigure.RedisAutoConfiguration;
import com.cxytiandi.kitty.jetcache.CachePlus;
import com.cxytiandi.kitty.jetcache.CachePlusImpl;
import com.cxytiandi.kitty.jetcache.config.HotKeyProperties;
import com.cxytiandi.kitty.jetcache.lock.DefaultLock;
import com.cxytiandi.kitty.jetcache.lock.Lock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import redis.clients.jedis.JedisPool;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-08-05 21:07
 */
@Configuration
@EnableConfigurationProperties(value = HotKeyProperties.class)
public class KittyJetCacheAutoConfiguration {

    @Autowired(required = false)
    private Lock lock;

    @CreateCache(name = "jetcache:lock:")
    private Cache cache;

    @Bean(name = "defaultPool")
    @DependsOn(RedisAutoConfiguration.AUTO_INIT_BEAN_NAME)
    public JedisPoolFactory defaultPool() {
        return new JedisPoolFactory("remote.default", JedisPool.class);
    }

    @Bean
    public CachePlus cache() {
        if (lock == null) {
            lock = new DefaultLock(cache);
        }
        return new CachePlusImpl(lock);
    }

}
