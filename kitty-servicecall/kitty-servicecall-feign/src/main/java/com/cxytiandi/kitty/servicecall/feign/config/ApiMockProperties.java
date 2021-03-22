package com.cxytiandi.kitty.servicecall.feign.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@Configuration
@ConfigurationProperties(prefix = "mock")
public class ApiMockProperties {

    /**
     * 资源：mock地址
     * 格式：GET:http://user-provider/user/{userId}##http://xxx.com/mock/api/1001
     */
    private List<String> apis;

    public String getMockApi(String resource) {
        if (CollectionUtils.isEmpty(apis)) {
            return null;
        }
        Map<String, String> apiMap = apis.stream().collect(Collectors.toMap(s -> {
            return s.split("##")[0];
        }, s -> s.split("##")[1]));
        return apiMap.get(resource);
    }
}
