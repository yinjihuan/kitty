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
@ConfigurationProperties(prefix = "sentinel.hot")
public class HotParamterProperties {

    /**
     * 资源对应的热点参数获取方式
     * params: URL参数中获取, params模式需要指定param名称，格式为params_paramName
     * path: 路径中获取, path模式需要指定path的格式，格式为/user/{id}
     * header: 请求头中获取, header模式需要指定header名称，格式为header_headerName
     * 格式：sentinel.hot.paramters[0]=test-resource:params_paramName
     * 资源名:获取方式
     */
    private List<String> paramters;

    public Map<String, Map<String, String>> getParamtersMap() {
        HashMap<String, Map<String, String>> map = new HashMap<>();

        if (CollectionUtils.isEmpty(paramters)) {
            return map;
        }

        for (String p : paramters) {
            String[] splits = p.split(":");
            HashMap<String, String> childMap = new HashMap<>();
            String value = splits[1];
            String model = value.substring(0, value.indexOf("_"));
            String modelValue = value.substring(value.indexOf("_") + 1);
            childMap.put(model, modelValue);
            map.put(splits[0], childMap);
        }

        return map;
    }
}
