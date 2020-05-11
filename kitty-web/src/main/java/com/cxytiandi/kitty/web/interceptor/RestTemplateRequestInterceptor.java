package com.cxytiandi.kitty.web.interceptor;

import com.cxytiandi.kitty.common.cat.CatConstantsExt;
import com.cxytiandi.kitty.common.cat.CatTransactionManager;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * RestTemplate 拦截器
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-05-11 21:20
 */
public class RestTemplateRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("URI",  request.getMethodValue() + request.getURI().toString());
        data.put("Params",  new String(body));

        String name = request.getURI().getHost() + request.getURI().getPath();
        return CatTransactionManager.newTransaction(() -> {
                    try {
                        return execution.execute(request, body);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }, CatConstantsExt.TYPE_CALL_RESTTEMPLATE, name);
    }
}
