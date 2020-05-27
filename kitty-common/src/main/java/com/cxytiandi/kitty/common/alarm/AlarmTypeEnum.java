package com.cxytiandi.kitty.common.alarm;

/**
 * 告警类型
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-05-26 21:19
 */
public enum AlarmTypeEnum {
    /**
     * 钉钉
     */
    DING_TALK("DingTalk"),
    /**
     * 外部系统
     */
    EXTERNAL_SYSTEM("ExternalSystem");

    AlarmTypeEnum(String type) {
        this.type = type;
    };

    private String type;

    public String getType() {
        return type;
    }
}
