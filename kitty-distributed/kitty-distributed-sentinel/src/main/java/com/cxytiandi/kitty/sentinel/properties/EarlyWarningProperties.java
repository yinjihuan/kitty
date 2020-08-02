package com.cxytiandi.kitty.sentinel.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-07-29 21:52
 */
@Data
@ConfigurationProperties(prefix = "sentinel.warn")
public class EarlyWarningProperties {

    /**
     * 资源对应的预警比例
     * 格式：sentinel.warn.proportions[0]=test-resource:0.5
     * 资源名:比例
     */
    private List<String> proportions;

    public Map<String, Double> getProportionMap() {
        HashMap<String, Double> map = new HashMap<>();

        if (CollectionUtils.isEmpty(proportions)) {
            return map;
        }

        for (String p : proportions) {
            String[] splits = p.split(":");
            map.put(splits[0], Double.parseDouble(splits[1]));
        }

        return map;
    }
}
