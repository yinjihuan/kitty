package com.cxytiandi.kitty.rocketmq;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.OrderProducerBean;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.aliyun.openservices.shade.org.apache.commons.lang3.StringUtils;
import com.cxytiandi.kitty.common.json.JsonUtils;

import java.util.concurrent.TimeUnit;

/**
 * RocketMq 消息发送
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-06-07 16:02
 */
public class RocketMqProducer {

    private ProducerBean producerBean;
    private OrderProducerBean orderProducerBean;

    public RocketMqProducer(ProducerBean producerBean, OrderProducerBean orderProducerBean) {
        this.producerBean = producerBean;
        this.orderProducerBean = orderProducerBean;
    }

    public SendResult send(Message message) {
        return producerBean.send(message);
    }

    public SendResult send(String topic, String tag, String body) {
        return send(buildMessage(topic, tag, null, body));
    }

    public <T> SendResult send(String topic, String tag, Class<T> body) {
        return send(buildMessage(topic, tag, null, JsonUtils.toJson(body)));
    }

    public SendResult send(String topic, String tag, String key, String body) {
        return send(buildMessage(topic, tag, key, body));
    }

    public <T> SendResult send(String topic, String tag, String key, Class<T> body) {
        return send(buildMessage(topic, tag, key, JsonUtils.toJson(body)));
    }

    public SendResult sendDelay(Message message, long delayTime, TimeUnit delayTimeUnit) {
        message.setStartDeliverTime(System.currentTimeMillis() + delayTimeUnit.toMinutes(delayTime));
        return send(message);
    }

    public SendResult sendDelay(String topic, String tag, String body, long delayTime, TimeUnit delayTimeUnit) {
        return send(buildMessage(topic, tag, null, body, delayTime, delayTimeUnit));
    }

    public <T> SendResult sendDelay(String topic, String tag, Class<T> body, long delayTime, TimeUnit delayTimeUnit) {
        return send(buildMessage(topic, tag, null, JsonUtils.toJson(body), delayTime, delayTimeUnit));
    }

    public SendResult sendDelay(String topic, String tag, String key, String body, long delayTime, TimeUnit delayTimeUnit) {
        return send(buildMessage(topic, tag, key, body, delayTime, delayTimeUnit));
    }

    public <T> SendResult sendDelay(String topic, String tag, String key, Class<T> body, long delayTime, TimeUnit delayTimeUnit) {
        return send(buildMessage(topic, tag, key, JsonUtils.toJson(body), delayTime, delayTimeUnit));
    }

    private Message buildMessage(String topic, String tag, String key, String body, long delayTime, TimeUnit delayTimeUnit) {
        Message message = new Message();
        message.setTopic(topic);
        message.setTag(tag);
        message.setBody(body.getBytes());
        if (StringUtils.isNotBlank(key)) {
            message.setKey(key);
        }
        if (delayTimeUnit != null && delayTime > 0) {
            message.setStartDeliverTime(System.currentTimeMillis() + delayTimeUnit.toMinutes(delayTime));
        }
        return message;
    }

    private Message buildMessage(String topic, String tag, String key, String body) {
       return buildMessage(topic, tag, key, body, 0, null);
    }

    public void sendOneway(Message message) {
        producerBean.sendOneway(message);
    }

    public void sendAsync(Message message, SendCallback sendCallback) {
        producerBean.sendAsync(message, sendCallback);
    }

}
