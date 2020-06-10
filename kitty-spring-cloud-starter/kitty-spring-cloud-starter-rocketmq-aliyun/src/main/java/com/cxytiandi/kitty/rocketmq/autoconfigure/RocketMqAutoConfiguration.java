package com.cxytiandi.kitty.rocketmq.autoconfigure;

import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.*;
import com.aliyun.openservices.ons.api.order.MessageOrderListener;
import com.aliyun.openservices.shade.com.google.common.collect.Maps;
import com.cxytiandi.kitty.rocketmq.RocketMQMessageListener;
import com.cxytiandi.kitty.rocketmq.RocketMqProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * RocketMq 自动配置
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-06-07 15:44
 */
@ImportAutoConfiguration(RocketMqProperties.class)
@Configuration
public class RocketMqAutoConfiguration {

    @Autowired(required = false)
    private List<MessageListener> messageListeners = new ArrayList<>();

    @Autowired(required = false)
    private List<MessageOrderListener> messageOrderListeners = new ArrayList<>();

    @Bean
    @ConditionalOnProperty(value = "kitty.rocketmq.aliyun.producer.enabled", matchIfMissing = true)
    public ProducerBean producerBean(RocketMqProperties rocketMqProperties) {
        ProducerBean producerBean = new ProducerBean();
        Properties properties = getProperties("Producer", rocketMqProperties);
        producerBean.setProperties(properties);
        return producerBean;
    }

    @Bean
    @ConditionalOnProperty(value = "kitty.rocketmq.aliyun.producer.enabled", matchIfMissing = true)
    public OrderProducerBean orderProducerBean(RocketMqProperties rocketMqProperties) {
        OrderProducerBean producerBean = new OrderProducerBean();
        Properties properties = getProperties("Producer", rocketMqProperties);
        producerBean.setProperties(properties);
        return producerBean;
    }

    @Bean
    public RocketMqProducer rocketMqProducer(ProducerBean producerBean, OrderProducerBean orderProducerBean) {
        return new RocketMqProducer(producerBean, orderProducerBean);
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnProperty(value = "kitty.rocketmq.aliyun.consumer.enabled", matchIfMissing = true)
    public ConsumerBean consumerBean(RocketMqProperties rocketMqProperties) {
        ConsumerBean consumerBean = new ConsumerBean();

        Properties properties = getProperties("Consumer", rocketMqProperties);
        consumerBean.setProperties(properties);

        consumerBean.setSubscriptionTable(getSubscriptionTable(messageListeners));

        return consumerBean;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnProperty(value = "kitty.rocketmq.aliyun.consumer.enabled", matchIfMissing = true)
    public OrderConsumerBean orderConsumerBean(RocketMqProperties rocketMqProperties) {
        OrderConsumerBean consumerBean = new OrderConsumerBean();

        Properties properties = getProperties("Consumer", rocketMqProperties);
        consumerBean.setProperties(properties);

        consumerBean.setSubscriptionTable(getSubscriptionTable(messageOrderListeners));

        return consumerBean;
    }

    private Map getSubscriptionTable(List listeners) {
        Map map = Maps.newHashMap();
        listeners.forEach(subscriber -> {
            if (subscriber.getClass().isAnnotationPresent(RocketMQMessageListener.class)) {
                RocketMQMessageListener listener = subscriber.getClass().getAnnotation(RocketMQMessageListener.class);
                map.put(getSubscription(listener), subscriber);
            }
        });
        return map;
    }

    private Subscription getSubscription(RocketMQMessageListener listener) {
        Subscription subscription = new Subscription();
        subscription.setTopic(listener.topic());
        subscription.setExpression(listener.tag());
        return subscription;
    }

    private Properties getProperties(String type, RocketMqProperties rocketMqProperties) {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.AccessKey, rocketMqProperties.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey, rocketMqProperties.getSecretKey());
        properties.put(PropertyKeyConst.NAMESRV_ADDR, rocketMqProperties.getNameServiceAddress());

        if ("Consumer".equals(type)) {
            if (rocketMqProperties.getSuspendTimeMillis() != null) {
                properties.put(PropertyKeyConst.SuspendTimeMillis, rocketMqProperties.getSuspendTimeMillis());
            }
            properties.put(PropertyKeyConst.GROUP_ID, rocketMqProperties.getGroupId());
            properties.put(PropertyKeyConst.MessageModel, rocketMqProperties.getMessageModel());
            properties.put(PropertyKeyConst.ConsumeThreadNums, rocketMqProperties.getConsumeThreadNums());
            properties.put(PropertyKeyConst.MaxReconsumeTimes, rocketMqProperties.getMaxReconsumeTimes());
            properties.put(PropertyKeyConst.ConsumeTimeout, rocketMqProperties.getConsumeTimeout());
            properties.put(PropertyKeyConst.MaxCachedMessageAmount, rocketMqProperties.getMaxCachedMessageAmount());
        }

        return properties;
    }

}
