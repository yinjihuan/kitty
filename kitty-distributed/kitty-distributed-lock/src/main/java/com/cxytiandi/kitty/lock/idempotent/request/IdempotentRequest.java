package com.cxytiandi.kitty.lock.idempotent.request;

import com.cxytiandi.kitty.lock.idempotent.enums.ReadWriteTypeEnum;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-04-02 22:14
 */
@Data
@Builder
public class IdempotentRequest {

    /**
     * 幂等Key
     */
    private String key;

    /**
     * 一级存储过期时间
     */
    private int firstLevelExpireTime;

    /**
     * 二级存储过期时间
     */
    private int secondLevelExpireTime;

    /**
     * 锁的过期时间
     */
    private int lockExpireTime;

    /**
     * 存储时间单位
     */
    private TimeUnit timeUnit;

    /**
     * 读写类型
     */
    private ReadWriteTypeEnum readWriteType;

}