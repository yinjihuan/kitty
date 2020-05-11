package com.cxytiandi.kitty.web.autoconfigure;

import com.cxytiandi.kitty.web.filter.CatServerFilter;
import com.cxytiandi.kitty.web.filter.IdempotentParamFilter;
import com.cxytiandi.kitty.web.interceptor.RestTemplateRequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Web自动配置
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-02-16 21:53
 */
@Configuration
public class WebAutoConfiguration {

    /**
     * 幂等ID使用：Cookie中标识用户信息的名称, 有值获取对应的，无值获取所有Cookie信息
     */
    @Value("${kitty.web.userCookieName:}")
    private String userCookieName;

    /**
     * 幂等ID使用：Header中标识用户信息的名称, 有值获取对应的，无值获取所有Header信息
     */
    @Value("${kitty.web.userHeaderName:}")
    private String userHeaderName;

    @Autowired(required = false)
    private List<RestTemplate> restTemplates;

    /**
     * 配置Cat Filter
     * @return
     */
    @Bean
    public FilterRegistrationBean catFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        CatServerFilter filter = new CatServerFilter();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setName("cat-filter");
        registration.setOrder(1);
        return registration;
    }

    /**
     * 配置幂等参数 Filter
     * @return
     */
    @Bean
    public FilterRegistrationBean idempotentParamtFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        IdempotentParamFilter filter = new IdempotentParamFilter(userCookieName, userHeaderName);
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setName("idempotent-filter");
        registration.setOrder(1);
        return registration;
    }

    /**
     *  RestTemplate 拦截器
     * @return
     */
    @Bean
    public RestTemplateRequestInterceptor restTemplateRequestInterceptor() {
        RestTemplateRequestInterceptor restTemplateRequestInterceptor = new RestTemplateRequestInterceptor();
        restTemplates.stream().forEach(restTemplate -> {
            List<ClientHttpRequestInterceptor> list = new ArrayList<>(restTemplate.getInterceptors());
            list.add(restTemplateRequestInterceptor);
            restTemplate.setInterceptors(list);
        });
        return restTemplateRequestInterceptor;
    }


}