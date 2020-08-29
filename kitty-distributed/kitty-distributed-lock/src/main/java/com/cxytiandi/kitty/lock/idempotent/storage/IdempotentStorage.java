package com.cxytiandi.kitty.lock.idempotent.storage;

import java.util.concurrent.TimeUnit;

/**
 * 幂等存储接口
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-08-17 22:25
 */
public interface IdempotentStorage {

    String COLL_NAME = "idempotent_record";

    IdempotentStorageTypeEnum type();

    void setValue(String key, String value, long expireTime, TimeUnit timeUnit);

    String getValue(String key);

}
