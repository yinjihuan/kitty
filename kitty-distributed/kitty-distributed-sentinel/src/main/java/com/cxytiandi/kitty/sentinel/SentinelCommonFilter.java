package com.cxytiandi.kitty.sentinel;

import com.alibaba.csp.sentinel.*;
import com.alibaba.csp.sentinel.adapter.servlet.callback.RequestOriginParser;
import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlCleaner;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.adapter.servlet.config.WebServletConfig;
import com.alibaba.csp.sentinel.adapter.servlet.util.FilterUtil;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义Sentinel的CommonFilter
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-07-21 22:40
 */
@Slf4j
public class SentinelCommonFilter implements Filter {
    private final static String ROOT_PATH = "/";
    private final static String HTTP_METHOD_SPECIFY = "HTTP_METHOD_SPECIFY";
    private final static String COLON = ":";
    private static final String EMPTY_ORIGIN = "";
    private boolean httpMethodSpecify = false;
    private Map<HandlerMethod,String> handlerMethodUrlMap = new ConcurrentHashMap<>(32);
    private DispatcherServlet dispatcherServlet;

    public SentinelCommonFilter(DispatcherServlet dispatcherServlet) {
        this.dispatcherServlet = dispatcherServlet;
    }

    @Override
    public void init(FilterConfig filterConfig) {
        httpMethodSpecify = Boolean.parseBoolean(filterConfig.getInitParameter(HTTP_METHOD_SPECIFY));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest sRequest = (HttpServletRequest)request;
        Entry urlEntry = null;

        try {
            String target = this.resolveTarget(sRequest);
            // Clean and unify the URL.
            // For REST APIs, you have to clean the URL (e.g. `/foo/1` and `/foo/2` -> `/foo/:id`), or
            // the amount of context and resources will exceed the threshold.
            UrlCleaner urlCleaner = WebCallbackManager.getUrlCleaner();
            if (urlCleaner != null) {
                target = urlCleaner.clean(target);
            }

            // If you intend to exclude some URLs, you can convert the URLs to the empty string ""
            // in the UrlCleaner implementation.
            if (!StringUtil.isEmpty(target)) {
                // Parse the request origin using registered origin parser.
                String origin = parseOrigin(sRequest);
                ContextUtil.enter(WebServletConfig.WEB_SERVLET_CONTEXT_NAME, origin);

                if (httpMethodSpecify) {
                    // Add HTTP method prefix if necessary.
                    String pathWithHttpMethod = sRequest.getMethod().toUpperCase() + COLON + target;
                    urlEntry = SphU.entry(pathWithHttpMethod, ResourceTypeConstants.COMMON_WEB, EntryType.IN);
                } else {
                    urlEntry = SphU.entry(target, ResourceTypeConstants.COMMON_WEB, EntryType.IN);
                }
            }
            chain.doFilter(request, response);
        } catch (BlockException e) {
            HttpServletResponse sResponse = (HttpServletResponse)response;
            // Return the block page, or redirect to another URL.
            WebCallbackManager.getUrlBlockHandler().blocked(sRequest, sResponse, e);
        } catch (IOException | ServletException | RuntimeException e2) {
            Tracer.traceEntry(e2, urlEntry);
            throw e2;
        } finally {
            if (urlEntry != null) {
                urlEntry.exit();
            }
            ContextUtil.exit();
        }
    }

    private String parseOrigin(HttpServletRequest request) {
        RequestOriginParser originParser = WebCallbackManager.getRequestOriginParser();
        String origin = EMPTY_ORIGIN;
        if (originParser != null) {
            origin = originParser.parseOrigin(request);
            if (StringUtil.isEmpty(origin)) {
                return EMPTY_ORIGIN;
            }
        }
        return origin;
    }

    @Override
    public void destroy() {

    }

    protected String resolveTarget(HttpServletRequest request) {
        String target = FilterUtil.filterTarget(request);

        String pattern = "";
        for (HandlerMapping mapping : dispatcherServlet.getHandlerMappings()) {
            HandlerExecutionChain handler = null;
            try {
                handler = mapping.getHandler(request);
                // handler hit, then resolve resource name from Controller and it's Controller method
                if (handler != null) {
                    Object handlerObject = handler.getHandler();
                    if (handlerObject instanceof HandlerMethod) {
                        HandlerMethod handlerMethod = (HandlerMethod)handlerObject;
                        //use it as cache
                        pattern = handlerMethodUrlMap.getOrDefault(handlerMethod,"");
                        if(StringUtils.isEmpty(pattern)){
                            //提取Controller方法上的注解值，拼装成Pattern
                            pattern = resolveResourceNameHandlerMethod(handlerMethod);
                            handlerMethodUrlMap.put(handlerMethod,pattern);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }

        // Clean and unify the URL.
        // For REST APIs, you have to clean the URL (e.g. `/foo/1` and `/foo/2` -> `/foo/:id`), or
        // the amount of context and resources will exceed the threshold.
        UrlCleaner urlCleaner = WebCallbackManager.getUrlCleaner();
        if (urlCleaner != null) {
            if(!StringUtils.isEmpty(pattern) && urlCleaner instanceof RestfulUrlCleaner){
                RestfulUrlCleaner restfulUrlCleaner = (RestfulUrlCleaner)urlCleaner;
                target = restfulUrlCleaner.clean(target,pattern);
            }else{
                target = urlCleaner.clean(target);
            }
        }
        return target;

    }

    /**
     * A HandlerMethod object usually represents  a controller's method which is annotated with
     *
     * @param handlerMethod An object that represents an Controller's Method
     * @return the resource name
     * @RequestMapping ,@GetMapping , @PostMapping, @DeleteMapping and so on,
     * so the resource can be represented with the controller's methods correspondingly.
     * Although Controller's methods is not good option to represent Resources.
     * As a result , the Annotations on Controllers and their methods can be introspected according to
     * Spring original mechanisms.
     * <p>
     * The Resource should use following patterns:
     * <Http_method>:<Controller-class-level-url-annotation><Controller-method-level-url-annotation>
     */
    private String resolveResourceNameHandlerMethod(HandlerMethod handlerMethod) {
        String target;
        String typeMapping = "";
        RequestMapping typeRequestMapping =
                AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(), RequestMapping.class);
        if (typeRequestMapping!=null && typeRequestMapping.value().length > 0) {
            typeMapping = typeRequestMapping.value()[0];
        }
        RequestMapping methodRequestMapping =
                AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(), RequestMapping.class);
        String methodMapping = methodRequestMapping.value()[0];
        if (typeMapping.length() > 1 && typeMapping.endsWith(ROOT_PATH)) {
            typeMapping = typeMapping.substring(0, typeMapping.length() - 1);
        }
        target = typeMapping + methodMapping;
        return target;
    }

}
