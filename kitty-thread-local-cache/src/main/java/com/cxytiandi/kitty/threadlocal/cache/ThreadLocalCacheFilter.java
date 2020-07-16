package com.cxytiandi.kitty.threadlocal.cache;

import lombok.extern.slf4j.Slf4j;
import javax.servlet.*;
import java.io.IOException;

/**
 * 线程缓存过滤器
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-07-12 19:46
 */
@Slf4j
public class ThreadLocalCacheFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(servletRequest, servletResponse);
        // 执行完后清除缓存
        ThreadLocalCacheManager.removeCache();
    }

}