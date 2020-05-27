package com.cxytiandi.kitty.threadpool;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import com.cxytiandi.kitty.threadpool.config.DynamicThreadPoolProperties;
import com.cxytiandi.kitty.threadpool.enums.QueueTypeEnum;
import com.cxytiandi.kitty.threadpool.enums.RejectedExecutionHandlerEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 动态线程池
 *
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
    private Map<String, KittyThreadPoolExecutor> threadPoolExecutorMap = new HashMap<>();

    /**
     * 存储线程池拒绝次数，Key:名称 Value:次数
     */
    private static Map<String, AtomicLong> threadPoolExecutorRejectCountMap = new ConcurrentHashMap<>();

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

    /**
     * 创建线程池
     * @param threadPoolProperties
     */
    private void createThreadPoolExecutor(DynamicThreadPoolProperties threadPoolProperties) {
        threadPoolProperties.getExecutors().forEach(executor -> {
            KittyThreadPoolExecutor threadPoolExecutor = new KittyThreadPoolExecutor(
                    executor.getCorePoolSize(),
                    executor.getMaximumPoolSize(),
                    executor.getKeepAliveTime(),
                    executor.getUnit(),
                    getBlockingQueue(executor.getQueueType(), executor.getQueueCapacity(), executor.isFair()),
                    new KittyThreadFactory(executor.getThreadPoolName()),
                    getRejectedExecutionHandler(executor.getRejectedExecutionType(), executor.getThreadPoolName()), executor.getThreadPoolName());

            threadPoolExecutorMap.put(executor.getThreadPoolName(), threadPoolExecutor);
        });
    }

    /**
     * 获取拒绝策略
     * @param rejectedExecutionType
     * @param threadPoolName
     * @return
     */
    private RejectedExecutionHandler getRejectedExecutionHandler(String rejectedExecutionType, String threadPoolName) {
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
        return new KittyAbortPolicy(threadPoolName);
    }

    /**
     * 获取阻塞队列
     * @param queueType
     * @param queueCapacity
     * @param fair
     * @return
     */
    private BlockingQueue getBlockingQueue(String queueType, int queueCapacity, boolean fair) {
        if (!QueueTypeEnum.exists(queueType)) {
            throw new RuntimeException("队列不存在 " + queueType);
        }
        if (QueueTypeEnum.ARRAY_BLOCKING_QUEUE.getType().equals(queueType)) {
            return new ArrayBlockingQueue(queueCapacity);
        }
        if (QueueTypeEnum.SYNCHRONOUS_QUEUE.getType().equals(queueType)) {
            return new SynchronousQueue(fair);
        }
        if (QueueTypeEnum.PRIORITY_BLOCKING_QUEUE.getType().equals(queueType)) {
            return new PriorityBlockingQueue(queueCapacity);
        }
        if (QueueTypeEnum.DELAY_QUEUE.getType().equals(queueType)) {
            return new DelayQueue();
        }
        if (QueueTypeEnum.LINKED_BLOCKING_DEQUE.getType().equals(queueType)) {
            return new LinkedBlockingDeque(queueCapacity);
        }
        if (QueueTypeEnum.LINKED_TRANSFER_DEQUE.getType().equals(queueType)) {
            return new LinkedTransferQueue();
        }
        return new ResizableCapacityLinkedBlockIngQueue(queueCapacity);
    }

    /**
     * 刷新线程池
     */
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
            threadPoolExecutor.setKeepAliveTime(executor.getKeepAliveTime(), executor.getUnit());
            threadPoolExecutor.setRejectedExecutionHandler(getRejectedExecutionHandler(executor.getRejectedExecutionType(), executor.getThreadPoolName()));
            BlockingQueue<Runnable> queue = threadPoolExecutor.getQueue();
            if (queue instanceof ResizableCapacityLinkedBlockIngQueue) {
                ((ResizableCapacityLinkedBlockIngQueue<Runnable>) queue).setCapacity(executor.getQueueCapacity());
            }
        });
    }

    public KittyThreadPoolExecutor getThreadPoolExecutor(String threadPoolName) {
        KittyThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolName);
        if (threadPoolExecutor == null) {
            throw new NullPointerException("找不到线程池 " + threadPoolName);
        }
        return threadPoolExecutor;
    }

    public AtomicLong getRejectCount(String threadPoolName) {
        return threadPoolExecutorRejectCountMap.get(threadPoolName);
    }

    public void clearRejectCount(String threadPoolName) {
        threadPoolExecutorRejectCountMap.remove(threadPoolName);
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

    static class KittyAbortPolicy implements RejectedExecutionHandler {

        private String threadPoolName;

        /**
         * Creates an {@code AbortPolicy}.
         */
        public KittyAbortPolicy() { }

        public KittyAbortPolicy(String threadPoolName) {
            this.threadPoolName = threadPoolName;
        }

        /**
         * Always throws RejectedExecutionException.
         *
         * @param r the runnable task requested to be executed
         * @param e the executor attempting to execute this task
         * @throws RejectedExecutionException always
         */
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            AtomicLong atomicLong = threadPoolExecutorRejectCountMap.putIfAbsent(threadPoolName, new AtomicLong(1));
            if (atomicLong != null) {
                atomicLong.incrementAndGet();
            }
            throw new RejectedExecutionException("Task " + r.toString() + " rejected from " + e.toString());
        }
    }

}
