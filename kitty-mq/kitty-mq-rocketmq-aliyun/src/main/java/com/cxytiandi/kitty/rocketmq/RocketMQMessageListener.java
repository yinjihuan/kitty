package com.cxytiandi.kitty.rocketmq;

import java.lang.annotation.*;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-06-07 16:42
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RocketMQMessageListener {

    /**
     * topic
     *
     * @return
     */
    String topic();

    /**
     * tag
     *
     * @return
     */
    String tag() default "*";
}
