package com.cxytiandi.kitty.threadpool;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-05-24 20:31
 */
@Slf4j
public class DynamicThreadPoolManager {

    @Autowired
    private DynamicThreadPoolProperties dynamicThreadPoolProperties;

    @Autowired
    private NacosConfigProperties nacosConfigProperties;

    /**
     * 存储线程池对象，Key:名称 Value:对象
     */
    private Map<String, ThreadPoolExecutor> threadPoolExecutorMap = new HashMap<>();

    @PostConstruct
    public void init() {
        createThreadPoolExecutor(dynamicThreadPoolProperties);
        initConfigUpdateListener(dynamicThreadPoolProperties);
    }

    /**
     * 监听配置修改，spring-cloud-alibaba 2.1.0版本不支持@NacosConfigListener的监听
     */
    public void initConfigUpdateListener(DynamicThreadPoolProperties dynamicThreadPoolProperties) {
        ConfigService configService = nacosConfigProperties.configServiceInstance();
        try {
            configService.addListener(dynamicThreadPoolProperties.getNacosDataId(), dynamicThreadPoolProperties.getNacosGroup(), new AbstractListener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    new Thread(() -> refreshThreadPoolExecutor()).start();
                    log.info("线程池配置有变化，刷新完成");
                }
            });
        } catch (NacosException e) {
            log.error("Nacos配置监听异常", e);
        }
    }

    private void createThreadPoolExecutor(DynamicThreadPoolProperties threadPoolProperties) {
        threadPoolProperties.getExecutors().forEach(executor -> {
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                    executor.getCorePoolSize(),
                    executor.getMaximumPoolSize(),
                    executor.getKeepAliveTime(),
                    executor.getUnit(),
                    getBlockingQueue(executor.getQueueType(), executor.getQueueCapacity()),
                    new KittyThreadFactory(executor.getThreadPoolName()),
                    getRejectedExecutionHandler(executor.getRejectedExecutionType()));

            threadPoolExecutorMap.put(executor.getThreadPoolName(), threadPoolExecutor);
        });
    }

    private RejectedExecutionHandler getRejectedExecutionHandler(String rejectedExecutionType) {
        if (!RejectedExecutionHandlerEnum.exists(rejectedExecutionType)) {
            throw new RuntimeException("拒绝策略不存在 " + rejectedExecutionType);
        }
        if (RejectedExecutionHandlerEnum.CALLER_RUNS_POLICY.getType().equals(rejectedExecutionType)) {
            return new ThreadPoolExecutor.CallerRunsPolicy();
        }
        if (RejectedExecutionHandlerEnum.DISCARD_OLDEST_POLICY.getType().equals(rejectedExecutionType)) {
            return new ThreadPoolExecutor.DiscardOldestPolicy();
        }
        if (RejectedExecutionHandlerEnum.DISCARD_POLICY.getType().equals(rejectedExecutionType)) {
            return new ThreadPoolExecutor.DiscardPolicy();
        }
        return new ThreadPoolExecutor.AbortPolicy();
    }

    private BlockingQueue getBlockingQueue(String queueType, int queueCapacity) {
        if (!QueueTypeEnum.exists(queueType)) {
            throw new RuntimeException("队列不存在 " + queueType);
        }
        if (QueueTypeEnum.ARRAY_BLOCKING_QUEUE.getType().equals(queueType)) {
            return new ArrayBlockingQueue(queueCapacity);
        }
        if (QueueTypeEnum.SYNCHRONOUS_QUEUE.getType().equals(queueType)) {
            return new SynchronousQueue(true);
        }
        if (QueueTypeEnum.PRIORITY_BLOCKING_QUEUE.getType().equals(queueType)) {
            return new PriorityBlockingQueue(queueCapacity);
        }
        return new LinkedBlockingQueue(queueCapacity);
    }

    private void refreshThreadPoolExecutor() {
        try {
            // 等待配置刷新完成
            Thread.sleep(1000);
        } catch (InterruptedException e) {

        }
        dynamicThreadPoolProperties.getExecutors().forEach(executor -> {
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(executor.getThreadPoolName());
            threadPoolExecutor.setCorePoolSize(executor.getCorePoolSize());
            threadPoolExecutor.setMaximumPoolSize(executor.getMaximumPoolSize());
        });
    }

    public ThreadPoolExecutor getThreadPoolExecutor(String threadPoolName) {
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolName);
        if (threadPoolExecutor == null) {
            throw new NullPointerException("找不到线程池 " + threadPoolName);
        }
        return threadPoolExecutor;
    }

    static class KittyThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        KittyThreadFactory(String threadPoolName) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = threadPoolName + "-" + poolNumber.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }
}
