package com.cxytiandi.kitty.rocketmq.autoconfigure;

import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.*;
import com.aliyun.openservices.ons.api.order.MessageOrderListener;
import com.aliyun.openservices.shade.com.google.common.collect.Maps;
import com.cxytiandi.kitty.lock.DistributedLock;
import com.cxytiandi.kitty.lock.autoconfigure.DistributedLockAutoConfiguration;
import com.cxytiandi.kitty.rocketmq.*;
import com.cxytiandi.kitty.rocketmq.properties.MessageWarningProperties;
import com.cxytiandi.kitty.rocketmq.properties.RocketMqProperties;
import com.cxytiandi.kitty.rocketmq.service.OnsTraceService;
import com.cxytiandi.kitty.rocketmq.service.TransactionMqService;
import com.cxytiandi.kitty.rocketmq.task.MessageCheckTask;
import com.cxytiandi.kitty.rocketmq.task.ProcessMessageTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
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
@Configuration
@AutoConfigureAfter({DistributedLockAutoConfiguration.class})
@ImportAutoConfiguration({RocketMqProperties.class, MessageWarningProperties.class})
public class RocketMqAutoConfiguration {

    @Autowired(required = false)
    private List<MessageListener> messageListeners = new ArrayList<>();

    @Autowired(required = false)
    private List<MessageOrderListener> messageOrderListeners = new ArrayList<>();

    @Autowired
    private DataSource dataSource;

    @Autowired
    private DistributedLock distributedLock;

    @Bean(initMethod = "start")
    @ConditionalOnProperty(value = "kitty.rocketmq.aliyun.producer.enabled", matchIfMissing = true)
    public ProducerBean producerBean(RocketMqProperties rocketMqProperties) {
        ProducerBean producerBean = new ProducerBean();
        Properties properties = getProperties("Producer", rocketMqProperties);
        producerBean.setProperties(properties);
        return producerBean;
    }

    @Bean(initMethod = "start")
    @ConditionalOnProperty(value = "kitty.rocketmq.aliyun.producer.enabled", matchIfMissing = true)
    public OrderProducerBean orderProducerBean(RocketMqProperties rocketMqProperties) {
        OrderProducerBean producerBean = new OrderProducerBean();
        Properties properties = getProperties("Producer", rocketMqProperties);
        producerBean.setProperties(properties);
        return producerBean;
    }

    @Bean
    public TransactionMqService transactionMQService() {
        return new TransactionMqService(new JdbcTemplate(dataSource));
    }

    @Bean
    public RocketMqProducer rocketMqProducer(ProducerBean producerBean, OrderProducerBean orderProducerBean,
                                             TransactionMqService transactionMQService) {
        return new RocketMqProducer(producerBean, orderProducerBean, transactionMQService);
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
            if (subscriber.getClass().isAnnotationPresent(RocketMqMessageListener.class)) {
                RocketMqMessageListener listener = subscriber.getClass().getAnnotation(RocketMqMessageListener.class);
                map.put(getSubscription(listener), subscriber);
            }
        });
        return map;
    }

    private Subscription getSubscription(RocketMqMessageListener listener) {
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

    @ConditionalOnProperty(value = "kitty.rocketmq.aliyun.autosend.enabled", matchIfMissing = true)
    @Bean(initMethod = "start")
    public ProcessMessageTask processMessageTask() {
        return new ProcessMessageTask(distributedLock);
    }

    @ConditionalOnProperty(value = "kitty.rocketmq.aliyun.msgcheck.enabled", matchIfMissing = true)
    @Bean
    public OnsTraceService onsTraceService(RocketMqProperties rocketMqProperties) {
        return new OnsTraceService(rocketMqProperties);
    }

    @ConditionalOnProperty(value = "kitty.rocketmq.aliyun.msgcheck.enabled", matchIfMissing = true)
    @Bean(initMethod = "start")
    public MessageCheckTask messageCheckTask() {
        return new MessageCheckTask(distributedLock);
    }

}
