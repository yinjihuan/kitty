package com.cxytiandi.kitty.sentinel.alarm;

import com.cxytiandi.kitty.common.alarm.AlarmManager;
import com.cxytiandi.kitty.common.alarm.AlarmMessage;
import com.cxytiandi.kitty.common.alarm.AlarmTypeEnum;
import com.cxytiandi.kitty.sentinel.properties.EarlyWarningProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * Sentinel 预告警
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-07-31 21:44
 */
public class SentinelBlockAlarm {

    /**
     * 应用名称，告警用到
     */
    @Value("${spring.application.name:unknown}")
    private String applicationName;

    @Autowired
    private EarlyWarningProperties earlyWarningProperties;

    @PostConstruct
    public void init() {
        new Thread(() -> {
            while (true) {
                String msg = SentinelBlockQueue.get();
                if (!StringUtils.isEmpty(msg)) {
                    sendAlarmMessage(msg);
                }
            }
        }).start();
    }

    private void sendAlarmMessage(String msg) {
        // 没有配置告警信息
        if (!StringUtils.hasText(earlyWarningProperties.getAlarmApiUrl()) && !StringUtils.hasText(earlyWarningProperties.getAccessToken())) {
            return;
        }

        AlarmMessage alarmMessage = AlarmMessage.builder()
                .alarmName("SentinelBlockEarlyAlarm")
                .alarmType(getAlarmType())
                .apiUrl(earlyWarningProperties.getAlarmApiUrl())
                .message(getAlarmMessage(msg, earlyWarningProperties))
                .accessToken(earlyWarningProperties.getAccessToken())
                .secret(earlyWarningProperties.getSecret())
                .alarmTimeInterval(earlyWarningProperties.getAlarmTimeInterval())
                .build();

        AlarmManager.sendAlarmMessage(alarmMessage);

    }


    private String getAlarmMessage(String reason, EarlyWarningProperties prop) {
        StringBuilder content = new StringBuilder();
        content.append("告警应用:").append(applicationName).append("\n");
        content.append("告警原因:").append(reason).append("\n");
        content.append("业务负责人:").append(prop.getOwner()).append("\n");
        content.append("告警间隔:").append(prop.getAlarmTimeInterval()).append("分钟\n");
        return content.toString();
    }


    private AlarmTypeEnum getAlarmType() {
        return StringUtils.hasText(earlyWarningProperties.getAlarmApiUrl()) ? AlarmTypeEnum.EXTERNAL_SYSTEM : AlarmTypeEnum.DING_TALK;
    }

}
