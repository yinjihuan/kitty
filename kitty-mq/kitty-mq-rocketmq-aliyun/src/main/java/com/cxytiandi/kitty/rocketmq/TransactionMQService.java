package com.cxytiandi.kitty.rocketmq;

import com.aliyun.openservices.ons.api.Message;
import com.cxytiandi.kitty.common.json.JsonUtils;
import org.springframework.jdbc.core.JdbcTemplate;

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

    public void saveTransactionMQMessage(Message message) {
        String sql = "insert into transaction_message(topic,tag,message_key,message_type,message) value(?,?,?,?,?)";
        jdbcTemplate.update(sql, message.getTopic(), message.getTag(), message.getKey(), "", JsonUtils.toJson(message));
    }

}
