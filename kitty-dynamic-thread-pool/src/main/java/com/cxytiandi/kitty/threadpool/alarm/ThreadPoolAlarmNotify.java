package com.cxytiandi.kitty.threadpool.alarm;

import com.cxytiandi.kitty.common.alarm.AlarmMessage;

/**
 * 线程池告警通知，使用者可实现改接口进行告警方式的扩展
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-05-27 22:13
 */
public interface ThreadPoolAlarmNotify {

    /**
     * 告警通知
     * @param alarmMessage
     */
    void alarmNotify(AlarmMessage alarmMessage);

}
