package com.cxytiandi.kitty.lock.idempotent.storage;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-08-25 22:51
 */
@Data
public class IdempotentRecord {

    @Id
    private String id;

    private String key;

    private String value;

    private Date addTime;

    private Date expireTime;

}
