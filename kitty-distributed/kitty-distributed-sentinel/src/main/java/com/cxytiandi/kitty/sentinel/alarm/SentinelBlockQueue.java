package com.cxytiandi.kitty.sentinel.alarm;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-07-31 21:10
 */
public class SentinelBlockQueue {

    private static LinkedBlockingQueue queue = new LinkedBlockingQueue();

    public static void add(String msg) {
        queue.add(msg);
    }

    public static String get() {
        Object msg = null;
        try {
            msg = queue.poll(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {

        }
        return msg == null ? null : msg.toString();
    }

}
