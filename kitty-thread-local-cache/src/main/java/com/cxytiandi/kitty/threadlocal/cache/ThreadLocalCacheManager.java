package com.cxytiandi.kitty.threadlocal.cache;

import java.util.Map;

/**
 * 线程内缓存管理
 *
 * @作者 尹吉欢
 * @时间 2020-07-12 10:47
 */
public class ThreadLocalCacheManager {

    private static ThreadLocal<Map> threadLocalCache = new ThreadLocal<>();

    public static void setCache(Map value) {
        threadLocalCache.set(value);
    }

    public static Map getCache() {
        return threadLocalCache.get();
    }

    public static void removeCache() {
        threadLocalCache.remove();
    }

    public static void removeCache(String key) {
        Map cache = threadLocalCache.get();
        if (cache != null) {
            cache.remove(key);
        }
    }

}
