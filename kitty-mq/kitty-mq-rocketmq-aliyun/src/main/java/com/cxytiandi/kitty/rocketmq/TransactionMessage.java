package com.cxytiandi.kitty.rocketmq;

import lombok.Data;

import java.util.Date;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-07-01 22:54
 */
@Data
public class TransactionMessage {

    private Long id;

    private String messageId;

    private String topic;

    private String tag;

    private String messageKey;

    private String messageType;

    private int status;

    private String message;

    private int sendCount;

    private Date sendTime;

    private Date addTime;


}
