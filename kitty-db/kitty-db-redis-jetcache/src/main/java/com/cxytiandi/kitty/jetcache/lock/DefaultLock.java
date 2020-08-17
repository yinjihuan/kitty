package com.cxytiandi.kitty.jetcache.lock;

import com.alicp.jetcache.AutoReleaseLock;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CreateCache;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
public class DefaultLock implements Lock {

	@CreateCache(name = "jetcache:cachePlus:")
	private Cache cache;

	public DefaultLock(Cache cache) {
		this.cache = cache;
	}

	@Override
	public <T> T lock(String key, Supplier<T> execute) {
		log.debug("开始加锁：" + key);
		try (AutoReleaseLock autoReleaseLock = cache.tryLock(key, 1, TimeUnit.MINUTES)) {
			if (autoReleaseLock != null) {
				return execute.get();
			}

			AutoReleaseLock lock = null;
			while(lock == null) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				lock = cache.tryLock(key, 1, TimeUnit.MINUTES);
			}

			try {
				return execute.get();
			} finally {
				lock.close();
			}
		}

	}
}
