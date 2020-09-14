package com.cxytiandi.kitty.rocketmq.service;

import com.aliyun.openservices.ons.api.Message;
import com.cxytiandi.kitty.common.json.JsonUtils;
import com.cxytiandi.kitty.rocketmq.TransactionMessage;
import com.cxytiandi.kitty.rocketmq.enums.RocketMqMessageTypeEnum;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-06-12 22:18
 */
public class TransactionMqService {

    private JdbcTemplate jdbcTemplate;

    public TransactionMqService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveTransactionMQMessage(Message message, RocketMqMessageTypeEnum rocketMessageEnum) {
        String sql = "insert into transaction_message(topic,tag,messageKey,messageType,message) value(?,?,?,?,?)";
        jdbcTemplate.update(sql, message.getTopic(), message.getTag(), message.getKey(), rocketMessageEnum.getType(), JsonUtils.toJson(message));
    }

    public void saveTransactionMQMessage(Message message) {
        saveTransactionMQMessage(message, RocketMqMessageTypeEnum.NORMAL);
    }

    public List<TransactionMessage> listWatingSendMessage(int size) {
        String sql = "select id,messageId,topic,tag,messageKey,messageType,status,message,sendCount,sendTime,addTime,updateTime,consumeFailCount from transaction_message where status = 0 limit ?";
        List<TransactionMessage> messages = jdbcTemplate.query(sql, new BeanPropertyRowMapper(TransactionMessage.class), size);
        return messages;
    }

    public void updateMessage(TransactionMessage message) {
        String sql = "update transaction_message set messageId = ?,status = ?,sendTime = ?,sendCount = ?,consumeFailCount = ?,traces = ? where id = ?";
        jdbcTemplate.update(sql, message.getMessageId(), message.getStatus(), message.getSendTime(), message.getSendCount(), message.getConsumeFailCount(), message.getTraces(), message.getId());
    }

    public DataSource getDataSource() {
        return jdbcTemplate.getDataSource();
    }

    public List<TransactionMessage> listWatingConsumeMessage(int size) {
        String sql = "select id,messageId,topic,tag,messageKey,messageType,status,message,sendCount,sendTime,addTime,updateTime,consumeFailCount from transaction_message where status = 1 limit ?";
        List<TransactionMessage> messages = jdbcTemplate.query(sql, new BeanPropertyRowMapper(TransactionMessage.class), size);
        return messages;
    }
}
