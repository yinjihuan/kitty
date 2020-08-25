package com.cxytiandi.kitty.lock.idempotent.properties;

import com.cxytiandi.kitty.lock.idempotent.storage.IdempotentStorageTypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-08-17 22:41
 */
@Data
@ConfigurationProperties(prefix = "kitty.idempontent")
public class IdempotentProperties {

    /**
     * 一级存储类型
     * @see IdempotentStorageTypeEnum
     */
    private String firstLevelType = IdempotentStorageTypeEnum.REDIS.name();

    /**
     * 二级存储类型
     * @see IdempotentStorageTypeEnum
     */
    private String secondLevelType;

}
