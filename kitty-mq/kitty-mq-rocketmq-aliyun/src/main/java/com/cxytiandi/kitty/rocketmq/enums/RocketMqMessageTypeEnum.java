package com.cxytiandi.kitty.rocketmq.enums;

/**
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-06-12 22:24
 */
public enum RocketMqMessageTypeEnum {

    /**
     * 有序消息
     */
    ORDER("order"),
    /**
     * 延迟消息
     */
    DELAY("delay"),
    /**
     * 普通消息
     */
    NORMAL("normal");

    RocketMqMessageTypeEnum(String type) {
        this.type = type;
    };

    private String type;

    public String getType() {
        return type;
    }

}
