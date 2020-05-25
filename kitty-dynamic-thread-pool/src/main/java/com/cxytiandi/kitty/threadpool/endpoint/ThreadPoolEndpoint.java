package com.cxytiandi.kitty.threadpool.endpoint;

import com.alibaba.fastjson.JSONObject;
import com.cxytiandi.kitty.threadpool.DynamicThreadPoolManager;
import com.cxytiandi.kitty.threadpool.config.DynamicThreadPoolProperties;
import com.cxytiandi.kitty.threadpool.KittyThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 线程池信息端点
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-05-25 22:08
 */
@Endpoint(id = "thread-pool")
public class ThreadPoolEndpoint {

    @Autowired
    private DynamicThreadPoolManager dynamicThreadPoolManager;

    @Autowired
    private DynamicThreadPoolProperties dynamicThreadPoolProperties;

    @ReadOperation
    public Map<String, Object> threadPools() {
        Map<String, Object> data = new HashMap<>();

        List<Map> threadPools = new ArrayList<>();
        dynamicThreadPoolProperties.getExecutors().forEach(prop -> {
            KittyThreadPoolExecutor executor = dynamicThreadPoolManager.getThreadPoolExecutor(prop.getThreadPoolName());
            AtomicLong rejectCount = dynamicThreadPoolManager.getRejectCount(prop.getThreadPoolName());

            Map<String, Object> pool = new HashMap<>();
            Map config = JSONObject.parseObject(JSONObject.toJSONString(prop), Map.class);
            pool.putAll(config);
            pool.put("activeCount", executor.getActiveCount());
            pool.put("completedTaskCount", executor.getCompletedTaskCount());
            pool.put("largestPoolSize", executor.getLargestPoolSize());
            pool.put("taskCount", executor.getTaskCount());
            pool.put("rejectCount", rejectCount == null ? 0 : rejectCount.get());
            threadPools.add(pool);
        });

        data.put("threadPools", threadPools);
        return data;
    }

}
