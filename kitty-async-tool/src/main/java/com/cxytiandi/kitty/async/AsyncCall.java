package com.cxytiandi.kitty.async;

import lombok.Data;

/**
 * 异步调用参数，多个任务一起并行调用
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-10-28 22:36
 */
@Data
public class AsyncCall<T, R> {

    /**
     * 任务ID, 区分不同的调用
     */
    private String taskId;

    /**
     * 执行参数
     */
    private T param;

    /**
     * 执行结果
     */
    private R result;

    public AsyncCall(String taskId, T param) {
        this.taskId = taskId;
        this.param = param;
    }
}
