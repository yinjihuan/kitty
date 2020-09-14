package com.cxytiandi.kitty.rocketmq;

import com.cxytiandi.kitty.rocketmq.enums.RocketMqMessageTypeEnum;
import lombok.Data;

import java.util.Date;

/**
 * 本地事务消息实体
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-07-01 22:54
 */
@Data
public class TransactionMessage {

    /**
     * 等待发送
     */
    public static final int WAIT_SEND = 0;

    /**
     * 等待消费
     */
    public static final int WAIT_CONSUME = 1;

    /**
     * 已消费
     */
    public static final int CONSOMED = 2;

    private Long id;

    /**
     * 消息发送成功后的消息ID
     */
    private String messageId;

    private String topic;

    private String tag;

    /**
     * 业务自定义Key
     */
    private String messageKey;

    /**
     * 消息类型
     * @see RocketMqMessageTypeEnum
     */
    private String messageType;

    /**
     * 状态：0等待发送  1已发送
     */
    private int status;

    /**
     * Message消息Json内容
     */
    private String message;

    /**
     * 重复发送消息次数
     */
    private int sendCount;

    /**
     * 最近发送消息时间
     */
    private Date sendTime;

    /**
     * 创建时间
     */
    private Date addTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 消费失败次数
     */
    private int consumeFailCount;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 消息轨迹
     */
    private String traces;
}
