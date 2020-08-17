package com.cxytiandi.kitty.jetcache;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.alicp.jetcache.CacheConstant;
import com.alicp.jetcache.embedded.CaffeineCacheBuilder;
import com.cxytiandi.kitty.common.cat.CatTransactionManager;
import com.cxytiandi.kitty.jetcache.config.HotKeyProperties;
import com.cxytiandi.kitty.jetcache.lock.Lock;
import com.cxytiandi.kitty.jetcache.utils.CacheKeyUtils;
import com.dianping.cat.Cat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.support.ConfigProvider;
import org.springframework.beans.factory.annotation.Value;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

import javax.annotation.PostConstruct;

@Slf4j
public class CachePlusImpl implements CachePlus {

	private Lock lock;

	@Autowired
	private Pool<Jedis> defaultPool;

	@Autowired
	protected ConfigProvider configProvider;

	@Autowired
	private HotKeyProperties hotKeyProperties;

	@Value("${jetcache.local.default.limit:1000}")
	private int localCacheLimit;

	@Value("${jetcache.local.default.expireAfterWriteInMillis:100}")
	private int expireAfterWriteInMillis;

	@Value("${jetcache.remote.default.valueEncoder:java}")
	private String remoteValueEncoder;

	@Value("${jetcache.remote.default.valueDecoder:java}")
	private String remoteValueDecoder;

	private Cache<String, Object> localCache = null;

	@PostConstruct
	public void init() {
		this.localCache = CaffeineCacheBuilder.createCaffeineCacheBuilder()
				.limit(localCacheLimit)
				.expireAfterWrite(expireAfterWriteInMillis, TimeUnit.SECONDS)
				.buildCache();
	}

	public CachePlusImpl() {

	}

	public CachePlusImpl(Lock lock) {
		this.lock = lock;

	}

	public <V, K> V getCache(String name, K key, Closure<V, K> closure) {
		return this.doGetCache(name, key, closure, 0, TimeUnit.SECONDS);
	}

	@Override
	public <V, K> V getCache(String name, K key, Closure<V, K> closure, long expire, TimeUnit timeUnit) {
		return this.doGetCache(name, key, closure, expire, timeUnit);
	}

	@Override
	public <V, K> V getLocalCache(String name, K key, Closure<V, K> closure) {
		return getLocalCache(name, key, closure, 0, TimeUnit.SECONDS);
	}

	@Override
	public <V, K> V getLocalCache(String name, K key, Closure<V, K> closure, long expire, TimeUnit timeUnit) {
		String jsonKey = CacheKeyUtils.convertKey(name, key);
		Object localValue = getCacheByCaffeine(jsonKey);
		if (localValue != null) {
			return (V) localValue;
		}

		return lock.lock(jsonKey, () -> {
			Object cacheData = getCacheByCaffeine(jsonKey);
			if (cacheData != null) {
				return (V) cacheData;
			}

			V result = closure.execute(key);
			CatTransactionManager.newTransaction(() -> {
				if (expire > 0) {
					localCache.put(jsonKey, result, expire, timeUnit);
				} else {
					localCache.put(jsonKey, result);
				}
			}, CacheConstant.CACHE_PUT, CacheConstant.CAFFEINE_PREFIX + jsonKey);

			return result;
		});
	}

	@SuppressWarnings("unchecked")
	public <V, K> V doGetCache(String name, K key, Closure<V, K> closure, long expire, TimeUnit timeUnit) {
		String jsonKey = CacheKeyUtils.convertKey(name, key);

		// 热点Key本地读取
		if (hotKeyProperties.hasHotKey(jsonKey)) {
			V localCacheResult = CatTransactionManager.newTransaction(() -> {
				Object localValue = localCache.get(jsonKey);
				if (localValue != null) {
					return (V) localValue;
				}
				return null;
			}, CacheConstant.CACHE_GET, CacheConstant.CAFFEINE_PREFIX + jsonKey);

			if (localCacheResult != null) {
				return localCacheResult;
			}
		}

		Jedis jedis = defaultPool.getResource();

		// 获取Key对应的缓存数据
		Object cacheResult = getCacheByRedis(jedis, jsonKey);
		if (cacheResult != null) {
			return (V) cacheResult;
		}

		// 没有缓存数据，调用加载逻辑进行数据缓存
		try {
			return lock.lock(jsonKey, () -> {
				Object cacheData = getCacheByRedis(jedis, jsonKey);
				if (cacheData != null) {
					return (V) cacheData;
				}
				V result = closure.execute(key);
				// 编码
				Function<Object, byte[]> f = configProvider.parseValueEncoder(remoteValueEncoder);
				byte[] data = f.apply(result);
				// 缓存
				CatTransactionManager.newTransaction(() -> {
					if (expire > 0) {
						jedis.set(jsonKey.getBytes(), data, "NX".getBytes(), "EX".getBytes(), timeUnit.toSeconds(expire));
					} else {
						jedis.set(jsonKey.getBytes(), data);
					}
				}, CacheConstant.CACHE_PUT, CacheConstant.REDIS_PREFIX + jsonKey);

				if (hotKeyProperties.hasHotKey(jsonKey)) {
					CatTransactionManager.newTransaction(() -> {
						if (expire > 0) {
							localCache.put(jsonKey, result, expire, timeUnit);
						} else {
							localCache.put(jsonKey, result);
						}
					}, CacheConstant.CACHE_PUT, CacheConstant.CAFFEINE_PREFIX + jsonKey);
				}

				return result;

			});

		} catch (Exception e) {
			log.error("", e);
		} finally {
			jedis.close();
		}

		return null;
	}

	private Object getCacheByRedis(Jedis jedis,  String jsonKey) {
		return CatTransactionManager.newTransaction(() -> {
			byte[] array = jedis.get(jsonKey.getBytes());
			if (array == null) {
				return null;
			}

			Object cacheData = null;
			try {
				// 获取到了就解码
				Function<byte[], Object> f = configProvider.parseValueDecoder(remoteValueDecoder);
				cacheData = f.apply(array);
			} catch (Exception e) {
				log.error("缓存解码异常", e);
				Cat.logError(e);
			} finally {
				jedis.close();
			}
			return cacheData;
		}, CacheConstant.CACHE_GET, CacheConstant.REDIS_PREFIX + jsonKey);
	}

	private Object getCacheByCaffeine(String jsonKey) {
		return CatTransactionManager.newTransaction(() -> {
			Object cacheData = localCache.get(jsonKey);
			return cacheData;
		}, CacheConstant.CACHE_GET, CacheConstant.CAFFEINE_PREFIX + jsonKey);
	}

}
