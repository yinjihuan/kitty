package com.cxytiandi.kitty.rocketmq;

import com.aliyun.openservices.ons.api.Message;
import com.cxytiandi.kitty.common.json.JsonUtils;
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
public class TransactionMQService {

    private JdbcTemplate jdbcTemplate;

    public TransactionMQService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveTransactionMQMessage(Message message, RocketMQMessageTypeEnum rocketMessageEnum) {
        String sql = "insert into transaction_message(topic,tag,message_key,message_type,message) value(?,?,?,?,?)";
        jdbcTemplate.update(sql, message.getTopic(), message.getTag(), message.getKey(), rocketMessageEnum.getType(), JsonUtils.toJson(message));
    }

    public void saveTransactionMQMessage(Message message) {
        saveTransactionMQMessage(message, RocketMQMessageTypeEnum.NORMAL);
    }

    public List<TransactionMessage> listWatingSendMessage(int size) {
        String sql = "select id,message_id,topic,tag,message_key,message_type,status,message,send_count,send_time,add_time from transaction_message where status = 0 limit ?";
        List<TransactionMessage> messages = jdbcTemplate.query(sql, new BeanPropertyRowMapper(TransactionMessage.class), size);
        return messages;
    }

    public void updateMessage(TransactionMessage message) {
        String sql = "update transaction_message set message_id = ?,status = ?,send_time = ?,send_count = ? where id = ?";
        jdbcTemplate.update(sql, message.getMessageId(), message.getStatus(), message.getSendTime(), message.getSendCount(), message.getId());
    }

    public DataSource getDataSource() {
        return jdbcTemplate.getDataSource();
    }
}
