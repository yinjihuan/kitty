package com.cxytiandi.kitty.id.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 分布式ID服务客户端-Segment模式
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-04-06 16:20
 */
@FeignClient("${kitty.id.segment.name:LeafSegment}")
public interface DistributedIdLeafSegmentRemoteService {

    @RequestMapping(value = "/api/segment/get/{key}")
    String getSegmentId(@PathVariable("key") String key);

}
