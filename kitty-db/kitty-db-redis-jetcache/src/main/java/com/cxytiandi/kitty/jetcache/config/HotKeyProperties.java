package com.cxytiandi.kitty.jetcache.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "jetcache.hotkeys")
public class HotKeyProperties {

	private List<String> hotKeys = new ArrayList<>();
	
	public boolean hasHotKey(String key) {
		return hotKeys.contains(key);
	}
}
