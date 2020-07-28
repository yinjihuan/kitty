package com.cxytiandi.kitty.sentinel;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlCleaner;
import org.springframework.util.CollectionUtils;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Restful 风格的资源处理
 * 参考：https://blog.csdn.net/luanlouis/article/details/91633042
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-07-21 22:01
 */
public class RestfulUrlCleaner implements UrlCleaner {

    private static final String ROOT_PATH = "/";

    private static final String EMPTY_STR = "";

    private static final Pattern PATH_VARIABLE = Pattern.compile("\\{(\\w+)\\}");

    private PathConfig pathConfig;

    public RestfulUrlCleaner(PathConfig pathConfig) {
        this.pathConfig = pathConfig;
    }

    @Override
    public String clean(String originUrl) {
        if (originUrl.endsWith(".ico")) {
            return EMPTY_STR;
        }
        List<String> ignoreUris = pathConfig.getIgnoreUris();
        if (!CollectionUtils.isEmpty(ignoreUris)) {
            if (ignoreUris.contains(originUrl)) {
                return EMPTY_STR;
            }
        }
        return originUrl;
    }

    public String clean(String originUrl, String pattern) {
        if (originUrl.startsWith(ROOT_PATH)) {
            originUrl = originUrl.substring(1);
        }

        if (pattern.startsWith(ROOT_PATH)) {
            pattern = pattern.substring(1);
        }

        String[] original = originUrl.split(ROOT_PATH);
        String[] patternArray = pattern.split(ROOT_PATH);
        if (original.length != patternArray.length) {
            return originUrl;
        }

        List<String> skipPaths = new ArrayList<>();
        if (!CollectionUtils.isEmpty(pathConfig.getSkipPaths())) {
            Map<String, List<String>> pathMap = new HashMap<>();
            pathConfig.getSkipPaths().forEach(s -> {
                String[] pathArray = s.split(":");
                pathMap.put(pathArray[0], Arrays.asList(pathArray[1].split(",")));
            });

            if (pathMap.containsKey(ROOT_PATH + pattern)) {
                skipPaths = pathMap.get(ROOT_PATH + pattern);
            }
        }

        Matcher matcher;
        StringBuilder replacedUrl = new StringBuilder();
        for (int i = 0; i < patternArray.length; i++) {
            replacedUrl.append(ROOT_PATH);
            matcher = PATH_VARIABLE.matcher(patternArray[i]);
            if (matcher.matches() && skipPaths.contains(matcher.group(1))) {
                replacedUrl.append(matcher.group(0));
            } else {
                replacedUrl.append(original[i]);
            }
        }

        return replacedUrl.toString();
    }
}
