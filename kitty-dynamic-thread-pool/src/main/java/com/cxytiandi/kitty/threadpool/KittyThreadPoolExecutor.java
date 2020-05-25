package com.cxytiandi.kitty.threadpool;

import com.cxytiandi.kitty.common.cat.CatTransactionManager;
import java.util.concurrent.*;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-05-25 22:36
 */
public class KittyThreadPoolExecutor extends ThreadPoolExecutor {

    /**
     * 线程池名称
     */
    private String threadPoolName;

    private String defaultTaskName = "defaultTask";

    /**
     * The default rejected execution handler
     */
    private static final RejectedExecutionHandler defaultHandler = new AbortPolicy();

    public KittyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public KittyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                              BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, defaultHandler);
    }

    public KittyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                   BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler, String threadPoolName) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.threadPoolName = threadPoolName;
    }

    @Override
    public void execute(Runnable command) {
        CatTransactionManager.newTransaction(() -> {
            super.execute(command);
        }, threadPoolName, defaultTaskName);
    }

    public void execute(Runnable command, String taskName) {
        CatTransactionManager.newTransaction(() -> {
            super.execute(command);
        }, threadPoolName, taskName);
    }

    public Future<?> submit(Runnable task, String taskName) {
        return CatTransactionManager.newTransaction(() -> {
            return super.submit(task);
        }, threadPoolName, taskName);
    }

    public <T> Future<T> submit(Callable<T> task, String taskName) {
        return CatTransactionManager.newTransaction(() -> {
            return super.submit(task);
        }, threadPoolName, taskName);
    }

    public <T> Future<T> submit(Runnable task, T result, String taskName) {
        return CatTransactionManager.newTransaction(() -> {
            return super.submit(task, result);
        }, threadPoolName, taskName);
    }

    public Future<?> submit(Runnable task) {
        return CatTransactionManager.newTransaction(() -> {
            return super.submit(task);
        }, threadPoolName, defaultTaskName);
    }

    public <T> Future<T> submit(Callable<T> task) {
        return CatTransactionManager.newTransaction(() -> {
            return super.submit(task);
        }, threadPoolName, defaultTaskName);
    }

    public <T> Future<T> submit(Runnable task, T result) {
        return CatTransactionManager.newTransaction(() -> {
            return super.submit(task, result);
        }, threadPoolName, defaultTaskName);
    }
}
