package com.cxytiandi.kitty.servicecall.feign;

import com.cxytiandi.kitty.cat.CatConstantsExt;
import com.cxytiandi.kitty.cat.CatContext;
import com.dianping.cat.Cat;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Feign拦截器，Cat消息树生成
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-02-18 21:48
 */
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        CatContext catContext = new CatContext();
        Cat.logRemoteCallClient(catContext,Cat.getManager().getDomain());
        template.header(CatConstantsExt.CAT_HTTP_HEADER_ROOT_MESSAGE_ID, catContext.getProperty(Cat.Context.ROOT));
        template.header(CatConstantsExt.CAT_HTTP_HEADER_PARENT_MESSAGE_ID, catContext.getProperty(Cat.Context.PARENT));
        template.header(CatConstantsExt.CAT_HTTP_HEADER_CHILD_MESSAGE_ID, catContext.getProperty(Cat.Context.CHILD));
    }

}