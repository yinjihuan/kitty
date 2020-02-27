package com.alicp.jetcache.embedded;

import com.alicp.jetcache.CacheConstant;
import com.alicp.jetcache.CacheValueHolder;
import com.cxytiandi.kitty.common.cat.CatTransactionManager;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created on 2016/10/25.
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class CaffeineCache<K, V> extends AbstractEmbeddedCache<K, V> {

    private com.github.benmanes.caffeine.cache.Cache cache;

    public CaffeineCache(EmbeddedCacheConfig<K, V> config) {
        super(config);
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        if (clazz.equals(com.github.benmanes.caffeine.cache.Cache.class)) {
            return (T) cache;
        }
        throw new IllegalArgumentException(clazz.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    protected InnerMap createAreaCache() {
        Caffeine<Object, Object> builder = Caffeine.newBuilder();
        builder.maximumSize(config.getLimit());
        final boolean isExpireAfterAccess = config.isExpireAfterAccess();
        final long expireAfterAccess = config.getExpireAfterAccessInMillis();
        builder.expireAfter(new Expiry<Object, CacheValueHolder>() {
            private long getRestTimeInNanos(CacheValueHolder value) {
                long now = System.currentTimeMillis();
                long ttl = value.getExpireTime() - now;
                if(isExpireAfterAccess){
                    ttl = Math.min(ttl, expireAfterAccess);
                }
                return TimeUnit.MILLISECONDS.toNanos(ttl);
            }

            @Override
            public long expireAfterCreate(Object key, CacheValueHolder value, long currentTime) {
                return getRestTimeInNanos(value);
            }

            @Override
            public long expireAfterUpdate(Object key, CacheValueHolder value,
                                          long currentTime, long currentDuration) {
                return currentDuration;
            }

            @Override
            public long expireAfterRead(Object key, CacheValueHolder value,
                                        long currentTime, long currentDuration) {
                return getRestTimeInNanos(value);
            }
        });

        builder.removalListener(((key, value, cause) -> {
            StringBuilder data = new StringBuilder();
            data.append("key=");
            data.append(key);
            data.append("&");
            data.append("cause=");
            data.append(cause.name());
            Cat.logEvent(CacheConstant.CACHE, CacheConstant.CAFFEINE_EXPIRE_EVENT + "_" + cause.name(), Message.SUCCESS, data.toString());
        }));

        cache = builder.build();
        return new InnerMap() {
            @Override
            public Object getValue(Object key) {
                try {
                    return CatTransactionManager.newTransaction(() -> {
                        return cache.getIfPresent(key);
                    }, CacheConstant.CACHE_GET, "Caffeine_" + key.toString(),null);
                } catch (Exception e){
                    Cat.logError(e);
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Map getAllValues(Collection keys) {
                try {
                    return CatTransactionManager.newTransaction(() -> {
                        return cache.getAllPresent(keys);
                    }, CacheConstant.CACHE_GET, "Caffeine_" + keys.stream().map(k -> k.toString()).collect(Collectors.joining(",")),null);
                } catch (Exception e){
                    Cat.logError(e);
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void putValue(Object key, Object value) {
                try {
                    CatTransactionManager.newTransaction(() -> {
                        cache.put(key, value);
                        return null;
                    }, CacheConstant.CACHE_PUT, "Caffeine_" + key.toString(), null);
                } catch (Exception e){
                    Cat.logError(e);
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void putAllValues(Map map) {
                cache.putAll(map);
            }

            @Override
            public boolean removeValue(Object key) {
                try {
                    return CatTransactionManager.newTransaction(() -> {
                        return cache.asMap().remove(key) != null;
                    }, CacheConstant.CACHE_REMOVE, "Caffeine_" + key.toString(), null);
                } catch (Exception e){
                    Cat.logError(e);
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void removeAllValues(Collection keys) {
                try {
                    CatTransactionManager.newTransaction(() -> {
                        cache.invalidateAll(keys);
                        return null;
                    }, CacheConstant.CACHE_REMOVE, "Caffeine_" + keys.stream().map(k -> k.toString()).collect(Collectors.joining(",")), null);
                } catch (Exception e){
                    Cat.logError(e);
                    throw new RuntimeException(e);
                }

            }

            @Override
            public boolean putIfAbsentValue(Object key, Object value) {
                try {
                    return CatTransactionManager.newTransaction(() -> {
                        return cache.asMap().putIfAbsent(key, value) == null;
                    }, CacheConstant.CACHE_PUT_IF_ABSENT, "Caffeine_" + key.toString(), null);
                } catch (Exception e){
                    Cat.logError(e);
                    throw new RuntimeException(e);
                }

            }
        };
    }
}