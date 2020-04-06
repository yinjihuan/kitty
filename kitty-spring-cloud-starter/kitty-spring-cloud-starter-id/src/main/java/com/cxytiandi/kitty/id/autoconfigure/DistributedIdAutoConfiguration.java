package com.cxytiandi.kitty.id.autoconfigure;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 分布式ID自动配置
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-02-16 19:31
 */
@Configuration
@EnableFeignClients("com.cxytiandi.kitty.id")
@ComponentScan("com.cxytiandi.kitty.id")
public class DistributedIdAutoConfiguration {

}