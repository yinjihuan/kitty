package com.cxytiandi.kitty.db.redis.spring;

import com.cxytiandi.kitty.common.cat.CatTransactionManager;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-04-28 22:11
 */
public class CatMonitorHelper {

    private int maxKeysLength = 10;

    private final String REDIS = "Redis";

    public void execute(String command, Runnable runnable) {
        Map<String, Object> data = new HashMap<>(1);
        CatTransactionManager.newTransaction(runnable, REDIS, command, data);
    }

    public void execute(String command, byte[] key, Runnable runnable) {
        Map<String, Object> data = new HashMap<>(1);
        data.put("key", deserialize(key));
        CatTransactionManager.newTransaction(runnable, REDIS, command, data);
    }

    public void execute(String command, byte[][] keys, Runnable runnable) {
        Map<String, Object> data = new HashMap<>(1);
        data.put("keys", toStringWithDeserialization(limitKeys(keys)));
        CatTransactionManager.newTransaction(runnable, REDIS, command, data);
    }

    public <T> T execute(String command, byte[] key, Supplier<T> supplier) {
        Map<String, Object> data = new HashMap<>(1);
        data.put("key", deserialize(key));
        return CatTransactionManager.newTransaction(supplier, REDIS, command, data);
    }

    public <T> T execute(String command, Supplier<T> supplier) {
        return CatTransactionManager.newTransaction(supplier, REDIS, command);
    }

    public <T> T execute(String command, byte[][] keys, Supplier<T> supplier) {
        Map<String, Object> data = new HashMap<>(1);
        data.put("keys", toStringWithDeserialization(limitKeys(keys)));
        return CatTransactionManager.newTransaction(supplier, REDIS, command, data);
    }

    private static String deserialize(byte[] bytes) {
        return (bytes == null ? "" : new String(bytes, StandardCharsets.UTF_8));
    }

    private static String toStringWithDeserialization(byte[][] array) {
        if (array == null) {
            return "";
        }

        List<String> list = new ArrayList<>();
        for (byte[] bytes : array) {
            list.add(deserialize(bytes));
        }

        return "[" + String.join(", ", list) + "]";
    }

    <T> T[] limitKeys(T[] keys) {
        if (keys != null && keys.length > maxKeysLength) {
            return Arrays.copyOfRange(keys, 0, maxKeysLength);
        }
        return keys;
    }
}
