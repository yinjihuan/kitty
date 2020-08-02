package com.cxytiandi.kitty.sentinel.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Sentinel Origin解析配置
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-07-21 22:12
 */
@Data
@ConfigurationProperties(prefix = "sentinel.origin.parser")
public class OriginParserProperties {

    /**
     * origin解析类型：remoteAddr,remoteHost,header
     */
    private String type = "remoteAddr";

    /**
     * 请求头解析时的名称
     */
    private String headerName = "service-name";

}
