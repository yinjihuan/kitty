package com.cxytiandi.kitty.cat.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Cat手动埋点注解
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-04-26 21:29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface CatTransaction {

    /**
     * 类型, 默认为Method
     * @return
     */
    String type() default "";

    /**
     * 名称, 默认为类名.方法名
     * @return
     */
    String name() default "";

    /**
     * 是否保存参数信息到Cat
     * @return
     */
    boolean isSaveParamToCat() default true;
}
