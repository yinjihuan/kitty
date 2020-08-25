package com.cxytiandi.kitty.lock.idempotent.exception;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-04-03 23:22
 */
public class IdempotentException extends RuntimeException {

    public IdempotentException(String message) {
        super(message);
    }

}