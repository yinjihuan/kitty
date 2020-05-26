package com.cxytiandi.kitty.threadpool.config;

import com.cxytiandi.kitty.threadpool.enums.QueueTypeEnum;
import com.cxytiandi.kitty.threadpool.enums.RejectedExecutionHandlerEnum;
import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * 线程池配置
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-05-24 20:36
 */
@Data
public class ThreadPoolProperties {

    /**
     * 线程池名称
     */
    private String threadPoolName = "KittyThreadPool";

    /**
     * 核心线程数
     */
    private int corePoolSize = 1;

    /**
     * 最大线程数, 默认值为CPU核心数量
     */
    private int maximumPoolSize = Runtime.getRuntime().availableProcessors();

    /**
     * 队列最大数量
     */
    private int queueCapacity = Integer.MAX_VALUE;

    /**
     * 队列类型
     * @see QueueTypeEnum
     */
    private String queueType = QueueTypeEnum.LINKED_BLOCKING_QUEUE.getType();

    /**
     * 拒绝策略
     * @see RejectedExecutionHandlerEnum
     */
    private String rejectedExecutionType = RejectedExecutionHandlerEnum.ABORT_POLICY.getType();

    /**
     * 空闲线程存活时间
     */
    private long keepAliveTime;

    /**
     * 空闲线程存活时间单位
     */
    private TimeUnit unit = TimeUnit.MILLISECONDS;

    /**
     * 队列容量阀值，超过此值告警
     */
    private int queueCapacityThreshold = queueCapacity;

}
