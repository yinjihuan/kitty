package com.cxytiandi.kitty.web.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 幂等参数过滤器
 *
 * 负责接收前端传递的幂等ID，如果没传递则生成幂等ID
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-04-04 19:46
 */
@Slf4j
public class IdempotentParamFilter implements Filter {

    /**
     * 幂等参数名称
     */
    private final String IDEMPOTEMT_ID_NAME = "globalIdempotentId";

    /**
     * 默认的空字符串
     */
    private final String DEFAULT_EMPTY_STR = "";

    /**
     * Cookie中标识用户信息的名称, 有值获取对应的，无值获取所有Cookie信息
     */
    private String userCookieName;

    /**
     * Header中标识用户信息的名称, 有值获取对应的，无值获取所有Header信息
     */
    private String userHeaderName;

    public IdempotentParamFilter(String userCookieName, String userHeaderName) {
        this.userCookieName = userCookieName;
        this.userHeaderName = userHeaderName;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        KittyRequestWrapper kittyRequestWrapper = new KittyRequestWrapper(request);

        String globalIdempotentId = getGlobalIdempotentId(request);
        if (!StringUtils.hasText(globalIdempotentId)) {
            globalIdempotentId = generateGlobalIdempotentId(request, kittyRequestWrapper.getRequestData());
        }

        if (StringUtils.hasText(globalIdempotentId)) {
            globalIdempotentId = DigestUtils.md5DigestAsHex(globalIdempotentId.getBytes());
            log.info("全局幂等ID {}", globalIdempotentId);
        }

        filterChain.doFilter(kittyRequestWrapper, servletResponse);
    }

    /**
     * 从请求信息中获取全局幂等ID
     * @param request
     * @return
     */
    private String getGlobalIdempotentId(HttpServletRequest request) {
        // 参数中获取
        String globalIdempotentId = request.getParameter(IDEMPOTEMT_ID_NAME);
        if (StringUtils.hasText(globalIdempotentId)) {
            return globalIdempotentId;
        }

        // 请求头中获取
        globalIdempotentId = request.getHeader(IDEMPOTEMT_ID_NAME);
        if (StringUtils.hasText(globalIdempotentId)) {
            return globalIdempotentId;
        }

        return DEFAULT_EMPTY_STR;
    }

    /**
     * 基于请求信息生成全局幂等ID
     * @param request
     * @return
     */
    private String generateGlobalIdempotentId(HttpServletRequest request, String requestBody) {
        String method = request.getMethod();
        if (HttpMethod.GET.matches(method) || HttpMethod.HEAD.matches(method)
                || HttpMethod.PATCH.matches(method) || HttpMethod.DELETE.matches(method)
                || HttpMethod.OPTIONS.matches(method) || HttpMethod.TRACE.matches(method)) {
            return "";
        }

        StringBuilder idempotentIdBuilder = new StringBuilder();
        // 请求体
        idempotentIdBuilder.append(requestBody);

        Cookie[] cookies = request.getCookies();

        // Cookie 有指定获取用户标识
        if (StringUtils.hasText(userCookieName)) {
            idempotentIdBuilder.append(getIdempotentCookieValue(userCookieName, cookies));
            return idempotentIdBuilder.toString();
        }

        // Header 有指定获取用户标识
        if (StringUtils.hasText(userHeaderName)) {
            idempotentIdBuilder.append(getIdempotentHeaderValue(userHeaderName, request));
            return idempotentIdBuilder.toString();
        }

        // 追加所有请求头
        idempotentIdBuilder.append(getIdempotentHeaderValue(request));

        // 追加所有Cookie
        idempotentIdBuilder.append(getIdempotentCookieValue(cookies));

        return idempotentIdBuilder.toString();
    }

    /**
     * 获取Header中幂等ID需要的信息
     * @param request
     * @return
     */
    private String getIdempotentHeaderValue(String headerName, HttpServletRequest request) {
        return request.getHeader(headerName);
    }

    /**
     * 获取Header中幂等ID需要的信息
     * @param request
     * @return
     */
    private String getIdempotentHeaderValue(HttpServletRequest request) {
        List<String> headerNameList = new ArrayList<>();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headerNameList.add(headerName);
        }

        return headerNameList.stream().map(h -> h + request.getHeader(h)).collect(Collectors.joining());
    }

    /**
     * 获取Cookie中幂等ID需要的信息
     * @param cookies
     * @return
     */
    private String getIdempotentCookieValue(String cookieName, Cookie[] cookies) {
        Optional<Cookie> optionalCookie = Arrays.asList(cookies).stream().filter(c -> cookieName.equals(c.getName())).findFirst();
        if (optionalCookie.isPresent()) {
            return cookieName +  optionalCookie.get().getValue();
        }
        return DEFAULT_EMPTY_STR;
    }


    /**
     * 获取Cookie中幂等ID需要的信息
     * @param cookies
     * @return
     */
    private String getIdempotentCookieValue(Cookie[] cookies) {
        return Arrays.asList(cookies).stream().map(c -> c.getName() + c.getValue()).collect(Collectors.joining());
    }

}