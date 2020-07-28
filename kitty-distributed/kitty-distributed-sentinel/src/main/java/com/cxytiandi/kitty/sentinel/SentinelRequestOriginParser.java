package com.cxytiandi.kitty.sentinel;

import com.alibaba.csp.sentinel.adapter.servlet.callback.RequestOriginParser;
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

    private OriginParserConfig originParserConfig;

    public SentinelRequestOriginParser(OriginParserConfig originParserConfig) {
        this.originParserConfig = originParserConfig;
    }

    @Override
    public String parseOrigin(HttpServletRequest request) {
        if (originParserConfig == null) {
            return null;
        }

        if ("remoteAddr".equals(originParserConfig.getType())) {
            return request.getRemoteAddr();
        }

        if ("remoteHost".equals(originParserConfig.getType())) {
            return request.getRemoteHost();
        }

        if ("header".equals(originParserConfig.getType())) {
            return request.getHeader(originParserConfig.getHeaderName());
        }

        return null;
    }
}
