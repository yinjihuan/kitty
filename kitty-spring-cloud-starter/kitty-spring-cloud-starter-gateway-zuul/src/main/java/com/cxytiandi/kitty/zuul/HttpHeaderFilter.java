package com.cxytiandi.kitty.zuul;

import com.cxytiandi.kitty.cat.CatConstantsExt;
import com.cxytiandi.kitty.cat.CatContext;
import com.dianping.cat.Cat;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-02-18 21:29
 */
public class HttpHeaderFilter extends ZuulFilter {

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        // 保存和传递CAT调用链上下文
        Cat.Context ctx = new CatContext();
        Cat.logRemoteCallClient(ctx);
        RequestContext requestContext = RequestContext.getCurrentContext();
        requestContext.addZuulRequestHeader(CatConstantsExt.CAT_HTTP_HEADER_ROOT_MESSAGE_ID, ctx.getProperty(Cat.Context.ROOT));
        requestContext.addZuulRequestHeader(CatConstantsExt.CAT_HTTP_HEADER_PARENT_MESSAGE_ID, ctx.getProperty(Cat.Context.PARENT));
        requestContext.addZuulRequestHeader(CatConstantsExt.CAT_HTTP_HEADER_CHILD_MESSAGE_ID, ctx.getProperty(Cat.Context.CHILD));
        return null;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }
}