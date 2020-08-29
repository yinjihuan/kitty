package com.cxytiandi.kitty.lock.idempotent.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-08-17 22:29
 */
@Slf4j
public class IdempotentStorageMongo implements IdempotentStorage {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public IdempotentStorageTypeEnum type() {
        return IdempotentStorageTypeEnum.MONGO;
    }

    @Override
    public void setValue(String key, String value, long expireTime, TimeUnit timeUnit) {
        log.debug("Mongo Set key:{}, Value:{}, expireTime:{}, timeUnit:{}", key, value, expireTime, timeUnit);
        Date date = new Date();

        IdempotentRecord record = new IdempotentRecord();
        record.setKey(key);
        record.setValue(value);
        record.setAddTime(date);

        long millis = timeUnit.toMillis(expireTime);
        record.setExpireTime(new Date(date.getTime() + millis));
        mongoTemplate.save(record, COLL_NAME);
    }

    @Override
    public String getValue(String key) {
        IdempotentRecord record = mongoTemplate.findOne(Query.query(Criteria.where("key").is(key)), IdempotentRecord.class, COLL_NAME);
        String value = record == null ? null : record.getValue();
        log.debug("Mongo Get key:{}, Value:{}", key, value);
        return value;
    }

}
