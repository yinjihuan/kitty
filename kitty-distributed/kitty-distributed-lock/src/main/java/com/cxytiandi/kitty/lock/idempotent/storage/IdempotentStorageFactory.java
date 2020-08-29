package com.cxytiandi.kitty.lock.idempotent.storage;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-08-17 22:36
 */
public class IdempotentStorageFactory {

    @Autowired
    private List<IdempotentStorage> idempotentStorageList;

    public IdempotentStorage getIdempotentStorage(IdempotentStorageTypeEnum type) {
        Optional<IdempotentStorage> idempotentStorageOptional = idempotentStorageList.stream().filter(t -> t.type() == type).findAny();
        return idempotentStorageOptional.orElseThrow(NullPointerException::new);
    }

}
