package com.cxytiandi.kitty.cat;


import com.dianping.cat.Cat;
import org.springframework.util.StringUtils;

/**
 * Cat Client初始化
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-02-16 19:31
 */
public class CatClientInit {

    public CatClientInit(String domain, String servers) {
        if (StringUtils.hasText(domain) && StringUtils.hasText(servers)) {
            Cat.initializeByDomain(domain, servers);
        } else if (StringUtils.hasText(domain)) {
            Cat.initializeByDomain(domain);
        } else {
            Cat.initialize();
        }
    }

}