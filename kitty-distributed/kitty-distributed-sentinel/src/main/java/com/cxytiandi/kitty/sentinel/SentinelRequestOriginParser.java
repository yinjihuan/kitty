package com.cxytiandi.kitty.sentinel;

import com.alibaba.csp.sentinel.adapter.servlet.callback.RequestOriginParser;
import com.cxytiandi.kitty.sentinel.properties.OriginParserProperties;

import javax.servlet.http.HttpServletRequest;

/**
 * Sentinel Origin解析
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-07-27 22:24
 */
public class SentinelRequestOriginParser implements RequestOriginParser {

    private OriginParserProperties originParserProperties;

    public SentinelRequestOriginParser(OriginParserProperties originParserProperties) {
        this.originParserProperties = originParserProperties;
    }

    @Override
    public String parseOrigin(HttpServletRequest request) {
        if (originParserProperties == null) {
            return null;
        }

        if ("remoteAddr".equals(originParserProperties.getType())) {
            return request.getRemoteAddr();
        }

        if ("remoteHost".equals(originParserProperties.getType())) {
            return request.getRemoteHost();
        }

        if ("header".equals(originParserProperties.getType())) {
            return request.getHeader(originParserProperties.getHeaderName());
        }

        return null;
    }
}
