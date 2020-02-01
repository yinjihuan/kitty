package com.cxytiandi.cat;


import com.dianping.cat.Cat;
import org.springframework.util.StringUtils;

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