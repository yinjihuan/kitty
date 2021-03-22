package com.cxytiandi.kitty.servicecall.feign;

import com.cxytiandi.kitty.common.context.ContextHolder;
import com.cxytiandi.kitty.common.context.RequestContext;
import com.cxytiandi.kitty.servicecall.feign.config.ApiMockProperties;
import feign.Client;
import feign.Request;
import feign.Response;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.openfeign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class MockLoadBalancerFeignClient extends LoadBalancerFeignClient {

    private ApiMockProperties apiMockProperties;

    public MockLoadBalancerFeignClient(Client delegate, CachingSpringLoadBalancerFactory lbClientFactory,
                                       SpringClientFactory clientFactory, ApiMockProperties apiMockProperties) {
        super(delegate, lbClientFactory, clientFactory);
        this.apiMockProperties = apiMockProperties;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        RequestContext currentContext = ContextHolder.getCurrentContext();
        String feignCallResourceName = currentContext.get("feignCallResourceName");
        String mockApi = apiMockProperties.getMockApi(feignCallResourceName);
        if (StringUtils.hasText(feignCallResourceName) && StringUtils.hasText(mockApi)) {
            Request newRequest = Request.create(request.httpMethod(),
                    mockApi, request.headers(), request.requestBody());
            return super.getDelegate().execute(newRequest, options);
        } else {
            return super.execute(request, options);
        }
    }

}
