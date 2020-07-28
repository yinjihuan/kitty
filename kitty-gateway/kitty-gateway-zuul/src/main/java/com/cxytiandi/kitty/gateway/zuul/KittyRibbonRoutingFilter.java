package com.cxytiandi.kitty.gateway.zuul;

import com.cxytiandi.kitty.common.cat.CatTransactionManager;
import com.cxytiandi.kitty.common.json.JsonUtils;
import com.dianping.cat.Cat;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.ribbon.support.RibbonCommandContext;
import org.springframework.cloud.netflix.ribbon.support.RibbonRequestCustomizer;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandFactory;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonRoutingFilter;
import org.springframework.cloud.netflix.zuul.util.ZuulRuntimeException;
import org.springframework.http.client.ClientHttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Zuul 路由Cat监控
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-02-18 14:56
 */
public class KittyRibbonRoutingFilter extends RibbonRoutingFilter {

    @Value("${spring.application.name:unknown}")
    private String applicationName;

    public KittyRibbonRoutingFilter(ProxyRequestHelper helper, RibbonCommandFactory<?> ribbonCommandFactory, List<RibbonRequestCustomizer> requestCustomizers) {
        super(helper, ribbonCommandFactory, requestCustomizers);
    }

    @Override
    public Object run() {

        RequestContext context = RequestContext.getCurrentContext();
        this.helper.addIgnoredHeaders();
        context.addZuulRequestHeader("service-name", applicationName);
        RibbonCommandContext commandContext = buildCommandContext(context);
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("serviceId", commandContext.getServiceId());
            data.put("method", commandContext.getMethod());
            data.put("retryable", commandContext.getRetryable());
            data.put("headers", JsonUtils.toJson(commandContext.getHeaders()));
            data.put("params", JsonUtils.toJson(commandContext.getParams()));

            return CatTransactionManager.newTransaction(() -> {
                try {
                    ClientHttpResponse response = forward(commandContext);
                    setResponse(response);
                    return response;
                }
                catch (ZuulException ex) {
                    Cat.logError(ex);
                    throw new ZuulRuntimeException(ex);
                }
                catch (Exception ex) {
                    Cat.logError(ex);
                    throw new ZuulRuntimeException(ex);
                }
            }, "HttpCall", "RibbonRouting", data);
        } catch (Exception e) {
            throw new ZuulRuntimeException(e);
        }
    }
}