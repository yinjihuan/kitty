package com.cxytiandi.kitty.id.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 分布式ID服务客户端-Snowflake模式
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-04-06 16:20
 */
@FeignClient("${kitty.id.snowflake.name:LeafSnowflake}")
public interface DistributedIdLeafSnowflakeRemoteService {

    @RequestMapping(value = "/api/snowflake/get/{key}")
    String getSnowflakeId(@PathVariable("key") String key);

}
