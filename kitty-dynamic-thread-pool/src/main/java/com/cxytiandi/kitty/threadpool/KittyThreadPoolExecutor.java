package com.cxytiandi.kitty.threadpool;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 自定义ThreadPoolExecutor
 *
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

    private Map<String, Transaction> transactionMap = new ConcurrentHashMap<>();

    private Map<String, String> runnableNameMap = new ConcurrentHashMap<>();

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
        runnableNameMap.putIfAbsent(command.getClass().getSimpleName(), defaultTaskName);
        super.execute(command);
    }

    public void execute(Runnable command, String taskName) {
        runnableNameMap.putIfAbsent(command.getClass().getSimpleName(), taskName);
        super.execute(command);
    }

    public Future<?> submit(Runnable task, String taskName) {
        runnableNameMap.putIfAbsent(task.getClass().getSimpleName(), taskName);
        return super.submit(task);
    }

    public <T> Future<T> submit(Callable<T> task, String taskName) {
        runnableNameMap.putIfAbsent(task.getClass().getSimpleName(), taskName);
        return super.submit(task);
    }

    public <T> Future<T> submit(Runnable task, T result, String taskName) {
        runnableNameMap.putIfAbsent(task.getClass().getSimpleName(), taskName);
        return super.submit(task, result);
    }

    public Future<?> submit(Runnable task) {
        runnableNameMap.putIfAbsent(task.getClass().getSimpleName(), defaultTaskName);
        return super.submit(task);
    }

    public <T> Future<T> submit(Callable<T> task) {
        runnableNameMap.putIfAbsent(task.getClass().getSimpleName(), defaultTaskName);
        return super.submit(task);
    }

    public <T> Future<T> submit(Runnable task, T result) {
        runnableNameMap.putIfAbsent(task.getClass().getSimpleName(), defaultTaskName);
        return super.submit(task, result);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        String threadName = Thread.currentThread().getName();
        Transaction transaction = Cat.newTransaction(threadPoolName, runnableNameMap.get(r.getClass().getSimpleName()));
        transactionMap.put(threadName, transaction);
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        String threadName = Thread.currentThread().getName();
        Transaction transaction = transactionMap.get(threadName);
        transaction.setStatus(Message.SUCCESS);
        if (t != null) {
            Cat.logError(t);
            transaction.setStatus(t);
        }
        transaction.complete();
        transactionMap.remove(threadName);
    }
}
