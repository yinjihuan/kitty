package com.cxytiandi.kitty.jetcache.lock;

import java.util.function.Supplier;

public interface Lock {

	<T> T lock(String key, Supplier<T> execute);
	

}
