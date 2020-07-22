package com.cxytiandi.kitty.sentinel;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-07-21 22:12
 */
@Data
@ConfigurationProperties(prefix = "sentinel.path")
public class PathConfig {

    /**
     * 访问uri中需要跳过的内容
     * 格式：sentinel.path.skipPaths[0]=/tt/test/{id}:id
     * 效果：访问uri为/tt/test/1 会被格式化为 /tt/test/{id}
     */
    private List<String> skipPaths;

}
