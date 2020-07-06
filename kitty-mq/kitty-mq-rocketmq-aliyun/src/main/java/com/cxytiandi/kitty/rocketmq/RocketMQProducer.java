package com.cxytiandi.kitty.rocketmq;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.OrderProducerBean;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.aliyun.openservices.shade.org.apache.commons.lang3.StringUtils;
import com.cxytiandi.kitty.common.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class RocketMQProducer {

    private ProducerBean producerBean;
    private OrderProducerBean orderProducerBean;
    private TransactionMQService transactionMQService;

    public RocketMQProducer(ProducerBean producerBean, OrderProducerBean orderProducerBean, TransactionMQService transactionMQService) {
        this.producerBean = producerBean;
        this.orderProducerBean = orderProducerBean;
        this.transactionMQService = transactionMQService;
    }

    public SendResult sendMessage(Message message) {
        try {
            SendResult result = producerBean.send(message);
            return result;
        } catch (Exception e) {
            log.error("sendMessage error", e);
            sendTransactionMessage(message);
        }
        return new SendResult();
    }

    public SendResult sendMessage(String topic, String tag, String body) {
        return sendMessage(buildMessage(topic, tag, null, body));
    }

    public <T> SendResult sendMessage(String topic, String tag, Class<T> body) {
        return sendMessage(buildMessage(topic, tag, null, JsonUtils.toJson(body)));
    }

    public SendResult sendMessage(String topic, String tag, String key, String body) {
        return sendMessage(buildMessage(topic, tag, key, body));
    }

    public <T> SendResult sendMessage(String topic, String tag, String key, Class<T> body) {
        return sendMessage(buildMessage(topic, tag, key, JsonUtils.toJson(body)));
    }

    public SendResult sendDelayMessage(Message message, long delayTime, TimeUnit delayTimeUnit) {
        message.setStartDeliverTime(System.currentTimeMillis() + delayTimeUnit.toMinutes(delayTime));
        return sendMessage(message);
    }

    public SendResult sendDelayMessage(String topic, String tag, String body, long delayTime, TimeUnit delayTimeUnit) {
        return sendMessage(buildMessage(topic, tag, null, body, delayTime, delayTimeUnit));
    }

    public <T> SendResult sendDelayMessage(String topic, String tag, Class<T> body, long delayTime, TimeUnit delayTimeUnit) {
        return sendMessage(buildMessage(topic, tag, null, JsonUtils.toJson(body), delayTime, delayTimeUnit));
    }

    public SendResult sendDelayMessage(String topic, String tag, String key, String body, long delayTime, TimeUnit delayTimeUnit) {
        return sendMessage(buildMessage(topic, tag, key, body, delayTime, delayTimeUnit));
    }

    public <T> SendResult sendDelayMessage(String topic, String tag, String key, Class<T> body, long delayTime, TimeUnit delayTimeUnit) {
        return sendMessage(buildMessage(topic, tag, key, JsonUtils.toJson(body), delayTime, delayTimeUnit));
    }

    public SendResult sendOrderMessage(Message message, String shardingKey) {
        try {
            return orderProducerBean.send(message, shardingKey);
        } catch (Exception e) {
            log.error("sendOrderMessage error", e);
            sendTransactionOrderMessage(message, shardingKey);
        }
        return new SendResult();
    }

    public SendResult sendOrderMessage(Message message) {
        return sendOrderMessage(message, message.getShardingKey());
    }

    public SendResult sendOrderMessage(String topic, String tag, String body, String shardingKey) {
        return sendOrderMessage(buildMessage(topic, tag, null, body, shardingKey));
    }

    public <T> SendResult sendOrderMessage(String topic, String tag, Class<T> body, String shardingKey) {
        return sendOrderMessage(buildMessage(topic, tag, null, JsonUtils.toJson(body), shardingKey));
    }

    public SendResult sendOrderMessage(String topic, String tag, String key, String body, String shardingKey) {
        return sendOrderMessage(buildMessage(topic, tag, key, body, shardingKey));
    }

    public <T> SendResult sendOrderMessage(String topic, String tag, String key, Class<T> body, String shardingKey) {
        return sendOrderMessage(buildMessage(topic, tag, key, JsonUtils.toJson(body), shardingKey));
    }

    public void sendTransactionMessage(Message message) {
        transactionMQService.saveTransactionMQMessage(message);
    }

    public void sendTransactionMessage(String topic, String tag, String body) {
        transactionMQService.saveTransactionMQMessage(buildMessage(topic, tag, null, body));
    }

    public <T> void sendTransactionMessage(String topic, String tag, Class<T> body) {
        transactionMQService.saveTransactionMQMessage(buildMessage(topic, tag, null, JsonUtils.toJson(body)));
    }

    public void sendTransactionMessage(String topic, String tag, String key, String body) {
        transactionMQService.saveTransactionMQMessage(buildMessage(topic, tag, key, body));
    }

    public <T> void sendTransactionMessage(String topic, String tag, String key, Class<T> body) {
        transactionMQService.saveTransactionMQMessage(buildMessage(topic, tag, key, JsonUtils.toJson(body)));
    }

    public void sendTransactionDelayMessage(Message message, long delayTime, TimeUnit delayTimeUnit) {
        message.setStartDeliverTime(System.currentTimeMillis() + delayTimeUnit.toMinutes(delayTime));
        transactionMQService.saveTransactionMQMessage(message, RocketMessageTypeEnum.DELAY);
    }

    public void sendTransactionDelayMessage(String topic, String tag, String body, long delayTime, TimeUnit delayTimeUnit) {
        transactionMQService.saveTransactionMQMessage(buildMessage(topic, tag, null, body, delayTime, delayTimeUnit), RocketMessageTypeEnum.DELAY);
    }

    public <T> void sendTransactionDelayMessage(String topic, String tag, Class<T> body, long delayTime, TimeUnit delayTimeUnit) {
        transactionMQService.saveTransactionMQMessage(buildMessage(topic, tag, null, JsonUtils.toJson(body), delayTime, delayTimeUnit), RocketMessageTypeEnum.DELAY);
    }

    public void sendTransactionDelayMessage(String topic, String tag, String key, String body, long delayTime, TimeUnit delayTimeUnit) {
        transactionMQService.saveTransactionMQMessage(buildMessage(topic, tag, key, body, delayTime, delayTimeUnit), RocketMessageTypeEnum.DELAY);
    }

    public <T> void sendTransactionDelayMessage(String topic, String tag, String key, Class<T> body, long delayTime, TimeUnit delayTimeUnit) {
        transactionMQService.saveTransactionMQMessage(buildMessage(topic, tag, key, JsonUtils.toJson(body), delayTime, delayTimeUnit), RocketMessageTypeEnum.DELAY);
    }

    public void sendTransactionOrderMessage(Message message, String shardingKey) {
        message.setShardingKey(shardingKey);
        transactionMQService.saveTransactionMQMessage(message, RocketMessageTypeEnum.ORDER);
    }

    public void sendTransactionOrderMessage(Message message) {
        transactionMQService.saveTransactionMQMessage(message, RocketMessageTypeEnum.ORDER);
    }

    public void sendTransactionOrderMessage(String topic, String tag, String body, String shardingKey) {
        transactionMQService.saveTransactionMQMessage(buildMessage(topic, tag, null, body, shardingKey), RocketMessageTypeEnum.ORDER);
    }

    public <T> void sendTransactionOrderMessage(String topic, String tag, Class<T> body, String shardingKey) {
        transactionMQService.saveTransactionMQMessage(buildMessage(topic, tag, null, JsonUtils.toJson(body), shardingKey), RocketMessageTypeEnum.ORDER);
    }

    public void sendTransactionOrderMessage(String topic, String tag, String key, String body, String shardingKey) {
        transactionMQService.saveTransactionMQMessage(buildMessage(topic, tag, key, body, shardingKey), RocketMessageTypeEnum.ORDER);
    }

    public <T> void sendTransactionOrderMessage(String topic, String tag, String key, Class<T> body, String shardingKey) {
        transactionMQService.saveTransactionMQMessage(buildMessage(topic, tag, key, JsonUtils.toJson(body), shardingKey), RocketMessageTypeEnum.ORDER);
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

    private Message buildMessage(String topic, String tag, String key, String body, String shardingKey) {
        Message message = buildMessage(topic, tag, key, body);
        message.setShardingKey(shardingKey);
        return message;
    }

    public void sendOneway(Message message) {
        producerBean.sendOneway(message);
    }

    public void sendAsync(Message message, SendCallback sendCallback) {
        producerBean.sendAsync(message, sendCallback);
    }

}
