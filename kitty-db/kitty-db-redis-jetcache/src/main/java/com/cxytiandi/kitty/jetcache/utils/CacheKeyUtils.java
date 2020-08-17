package com.cxytiandi.kitty.jetcache.utils;

import com.alicp.jetcache.support.FastjsonKeyConvertor;

public class CacheKeyUtils {

	public static String convertKey(String name, Object key) {
		String jsonKey = "[]";
		if (key != null) {
			 jsonKey = name + "." + FastjsonKeyConvertor.INSTANCE.apply(new Object[] { key }).toString();
		}
		return jsonKey;
	}
	
	public static String convertKey(Object key) {
		String jsonKey = "[]";
		if (key != null) {
			 jsonKey = FastjsonKeyConvertor.INSTANCE.apply(new Object[] { key }).toString();
		}
		return jsonKey;
	}

}
