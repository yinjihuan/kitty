package com.cxytiandi.kitty.jetcache;

import java.util.concurrent.TimeUnit;

public interface CachePlus {
	
    <V, K> V getCache(String name, K key, Closure<V, K> closure);
	
    <V, K> V getCache(String name, K key, Closure<V, K> closure, long expire, TimeUnit timeUnit);

    <V, K> V getLocalCache(String name, K key, Closure<V, K> closure);

    <V, K> V getLocalCache(String name, K key, Closure<V, K> closure, long expire, TimeUnit timeUnit);
	
}
