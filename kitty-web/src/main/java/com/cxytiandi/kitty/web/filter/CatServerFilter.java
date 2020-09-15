package com.cxytiandi.kitty.web.filter;


import com.cxytiandi.kitty.common.cat.CatConstantsExt;
import com.cxytiandi.kitty.common.cat.CatContext;
import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Cat 监控过滤器
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-02-18 21:06
 */
public class CatServerFilter implements Filter {

    @Value("${spring.application.name:unknown}")
    private String applicationName;

    public CatServerFilter(String applicationName) {
        this.applicationName = applicationName;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        ContentCachingRequestWrapper wrapperRequest = new ContentCachingRequestWrapper(request);
        String uri = request.getRequestURI();

        // 构建远程消息树
        if(request.getHeader(CatConstantsExt.CAT_HTTP_HEADER_ROOT_MESSAGE_ID) != null){
            CatContext catContext = new CatContext();
            catContext.addProperty(Cat.Context.ROOT,request.getHeader(CatConstantsExt.CAT_HTTP_HEADER_ROOT_MESSAGE_ID));
            catContext.addProperty(Cat.Context.PARENT,request.getHeader(CatConstantsExt.CAT_HTTP_HEADER_PARENT_MESSAGE_ID));
            catContext.addProperty(Cat.Context.CHILD,request.getHeader(CatConstantsExt.CAT_HTTP_HEADER_CHILD_MESSAGE_ID));
            Cat.logRemoteCallServer(catContext);
        }

        Transaction filterTransaction = Cat.newTransaction(CatConstants.TYPE_URL, request.getMethod() + ":"+uri);

        try {
            Cat.logEvent(CatConstantsExt.TYPE_URL_METHOD, request.getMethod(), Message.SUCCESS, request.getRequestURL().toString());
            Cat.logEvent(CatConstantsExt.TYPE_URL_CLIENT, request.getRemoteHost() + "【" + applicationName + "】");

            filterChain.doFilter(wrapperRequest, servletResponse);
            filterTransaction.setSuccessStatus();
        } catch (Exception e) {
            filterTransaction.setStatus(e);
            Cat.logError("请求体：" + getRequestBody(wrapperRequest), e);
            throw e;
        } finally {
            filterTransaction.complete();
        }
    }
    private String getRequestBody(ContentCachingRequestWrapper req) {
        try {
            return IOUtils.toString(req.getContentAsByteArray(), "UTF-8");
        } catch (IOException e) {

        }
        return "";
    }
    @Override
    public void destroy() {

    }
}