package com.cxytiandi.kitty.common.alarm;

import com.cxytiandi.kitty.common.json.JsonUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-05-25 22:56
 */
public class AlarmManager {

    /**
     * 存储上次告警的时间，Key:名称 Value:时间戳
     */
    private static Map<String, AtomicLong> threadPoolExecutorAlarmTimeMap = new ConcurrentHashMap<>();

    /**
     * 发送告警消息
     * @param alarmMessage
     */
    public static void sendAlarmMessage(AlarmMessage alarmMessage) {
        AtomicLong alarmTime = threadPoolExecutorAlarmTimeMap.get(alarmMessage.getAlarmName());
        if (alarmTime != null && (alarmTime.get() + alarmMessage.getAlarmTimeInterval() * 60 * 1000) > System.currentTimeMillis()) {
            return;
        }
        if (alarmMessage.getAlarmType() == AlarmTypeEnum.DING_TALK) {
            DingDingMessageUtil.sendTextMessage(alarmMessage.getAccessToken(), alarmMessage.getSecret(), alarmMessage.getMessage());
        }

        if (alarmMessage.getAlarmType() == AlarmTypeEnum.EXTERNAL_SYSTEM) {
            Map<String, String> data = new HashMap<>(2);
            data.put("alarmName", alarmMessage.getAlarmName());
            data.put("message", alarmMessage.getMessage());
            DingDingMessageUtil.post(alarmMessage.getApiUrl(), JsonUtils.toJson(data));
        }

        threadPoolExecutorAlarmTimeMap.put(alarmMessage.getAlarmName(), new AtomicLong(System.currentTimeMillis()));

    }

}
