package com.cxytiandi.kitty.lock.idempotent;

import com.cxytiandi.kitty.lock.DistributedLock;
import com.cxytiandi.kitty.lock.idempotent.enums.ReadWriteTypeEnum;
import com.cxytiandi.kitty.lock.idempotent.properties.IdempotentProperties;
import com.cxytiandi.kitty.lock.idempotent.request.IdempotentRequest;
import com.cxytiandi.kitty.lock.idempotent.storage.IdempotentStorage;
import com.cxytiandi.kitty.lock.idempotent.storage.IdempotentStorageFactory;
import com.cxytiandi.kitty.lock.idempotent.storage.IdempotentStorageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 幂等实现
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-04-02 22:31
 */
@Slf4j
public class DistributedIdempotentImpl implements DistributedIdempotent {

    @Autowired
    private DistributedLock distributedLock;

    @Autowired
    private IdempotentProperties idempotentProperties;

    @Autowired
    private IdempotentStorageFactory idempotentStorageFactory;

    /**
     * 锁名称后缀，区分锁和幂等的Key
     */
    private String lockSuffix = "_lock";

    /**
     * 幂等Key对应的默认值
     */
    private String idempotentDefaultValue = "1";

    @Override
    public <T> T execute(String key, int lockExpireTime, int firstLevelExpireTime, int secondLevelExpireTime, TimeUnit timeUnit, ReadWriteTypeEnum readWriteType, Supplier<T> execute, Supplier<T> fail) {
        IdempotentRequest idempotentRequest = IdempotentRequest.builder().key(key)
                .lockExpireTime(lockExpireTime)
                .firstLevelExpireTime(firstLevelExpireTime)
                .secondLevelExpireTime(secondLevelExpireTime)
                .timeUnit(timeUnit)
                .readWriteType(readWriteType)
                .build();
        return execute(idempotentRequest, execute, fail);
    }

    @Override
    public <T> T execute(String key, int lockExpireTime, int firstLevelExpireTime, int secondLevelExpireTime, TimeUnit timeUnit, Supplier<T> execute, Supplier<T> fail) {
        return execute(key, lockExpireTime, firstLevelExpireTime, secondLevelExpireTime, timeUnit, ReadWriteTypeEnum.PARALLEL, execute, fail);
    }

    private <T> T orderExecute(IdempotentStorage firstIdempotentStorage, IdempotentStorage secondIdempotentStorage, IdempotentRequest request, Object execute, Object fail) {
        String firstValue = firstIdempotentStorage.getValue(request.getKey());
        String secondValue = null;
        if (secondIdempotentStorage != null) {
            secondValue = secondIdempotentStorage.getValue(request.getKey());
        }

        // 一级和二级存储中都没有数据，表示可以继续执行
        if (!StringUtils.hasText(firstValue) && !StringUtils.hasText(secondValue)) {
            T executeResult = getExecuteResult(execute);

            firstIdempotentStorage.setValue(request.getKey(), idempotentDefaultValue, request.getFirstLevelExpireTime(), request.getTimeUnit());
            if (secondIdempotentStorage != null) {
                secondIdempotentStorage.setValue(request.getKey(), idempotentDefaultValue, request.getSecondLevelExpireTime(), request.getTimeUnit());
            }

            return executeResult;
        }

        // 不能继续往下执行
        if (execute instanceof Supplier) {
            Supplier<T> failSupplier = (Supplier<T>) fail;
            return failSupplier.get();
        } else {
            Runnable failRunnable = (Runnable) execute;
            failRunnable.run();
            return null;
        }
    }

    private <T> T parallelExecute(IdempotentStorage firstIdempotentStorage, IdempotentStorage secondIdempotentStorage, IdempotentRequest request, Object execute, Object fail) {
        List<String> results = getParallelExecuteResults(request.getKey(), firstIdempotentStorage, secondIdempotentStorage);
        if (results.stream().filter(StringUtils::hasText).count() == 0) {
            T executeResult = getExecuteResult(execute);
            parallelWriteResults(firstIdempotentStorage, secondIdempotentStorage, request);
            return executeResult;
        }

        if (execute instanceof Supplier) {
            Supplier<T> failSupplier = (Supplier<T>) fail;
            return failSupplier.get();
        } else {
            Runnable failRunnable = (Runnable) fail;
            failRunnable.run();
            return null;
        }
    }

    private <T> T getExecuteResult(Object execute) {
        T executeResult = null;
        if (execute instanceof Supplier) {
            Supplier<T> executeSupplier = (Supplier<T>) execute;
            executeResult = executeSupplier.get();
        } else {
            Runnable executeRunnable = (Runnable) execute;
            executeRunnable.run();
        }
        return executeResult;
    }

    private void parallelWriteResults(IdempotentStorage firstIdempotentStorage, IdempotentStorage secondIdempotentStorage, IdempotentRequest request) {
        CompletableFuture<String> firstWriteFuture = CompletableFuture.supplyAsync(() -> {
            firstIdempotentStorage.setValue(request.getKey(), idempotentDefaultValue, request.getFirstLevelExpireTime(), request.getTimeUnit());
            return null;
        });
        CompletableFuture<String> secondWriteFuture = CompletableFuture.supplyAsync(() -> {
            if (StringUtils.hasText(idempotentProperties.getSecondLevelType())) {
                secondIdempotentStorage.setValue(request.getKey(), idempotentDefaultValue, request.getSecondLevelExpireTime(), request.getTimeUnit());
            }
            return null;
        });

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(firstWriteFuture, secondWriteFuture);
        try {
            combinedFuture.get();
        } catch (Exception e) {
            log.error("并行写异常", e);
        }
    }

    private List<String> getParallelExecuteResults(String key, IdempotentStorage firstIdempotentStorage, IdempotentStorage secondIdempotentStorage) {
        CompletableFuture<String> firstReadFuture = CompletableFuture.supplyAsync(() -> {
            return firstIdempotentStorage.getValue(key);
        });

        CompletableFuture<String> secondReadFuture = CompletableFuture.supplyAsync(() -> {
            if (secondIdempotentStorage != null) {
                return secondIdempotentStorage.getValue(key);
            }
            return null;
        });

        CompletableFuture<Void> readCombinedFuture = CompletableFuture.allOf(firstReadFuture, secondReadFuture);
        try {
            readCombinedFuture.get();
        } catch (Exception e) {
            log.error("并行读异常", e);
        }

        return Stream.of(firstReadFuture, secondReadFuture).map(CompletableFuture::join).collect(Collectors.toList());
    }

    @Override
    public <T> T execute(IdempotentRequest request, Supplier<T> execute, Supplier<T> fail) {
        return distributedLock.lock(request.getKey() + lockSuffix, request.getLockExpireTime(), request.getTimeUnit(), () -> {
            IdempotentStorage secondIdempotentStorage = null;
            if (StringUtils.hasText(idempotentProperties.getSecondLevelType())) {
                secondIdempotentStorage = idempotentStorageFactory.getIdempotentStorage(IdempotentStorageTypeEnum.valueOf(idempotentProperties.getSecondLevelType()));
            }

            IdempotentStorage firstIdempotentStorage = idempotentStorageFactory.getIdempotentStorage(IdempotentStorageTypeEnum.valueOf(idempotentProperties.getFirstLevelType()));


            if (request.getReadWriteType() == ReadWriteTypeEnum.ORDER) {
                return orderExecute(firstIdempotentStorage, secondIdempotentStorage, request, execute, fail);
            }

            if (request.getReadWriteType() == ReadWriteTypeEnum.PARALLEL) {
                return parallelExecute(firstIdempotentStorage, secondIdempotentStorage, request, execute, fail);
            }

            return fail.get();
        }, fail);
    }

    @Override
    public void execute(String key, int lockExpireTime, int firstLevelExpireTime, int secondLevelExpireTime, TimeUnit timeUnit, ReadWriteTypeEnum readWriteType, Runnable execute, Runnable fail) {
        IdempotentRequest idempotentRequest = IdempotentRequest.builder().key(key)
                .lockExpireTime(lockExpireTime)
                .firstLevelExpireTime(firstLevelExpireTime)
                .secondLevelExpireTime(secondLevelExpireTime)
                .timeUnit(timeUnit)
                .readWriteType(readWriteType)
                .build();
        execute(idempotentRequest, execute, fail);
    }

    @Override
    public void execute(String key, int lockExpireTime, int firstLevelExpireTime, int secondLevelExpireTime, TimeUnit timeUnit, Runnable execute, Runnable fail) {
        execute(key, lockExpireTime, firstLevelExpireTime, secondLevelExpireTime, timeUnit, ReadWriteTypeEnum.PARALLEL, execute, fail);
    }

    @Override
    public void execute(IdempotentRequest request, Runnable execute, Runnable fail) {
        distributedLock.lock(request.getKey() + lockSuffix, request.getLockExpireTime(), request.getTimeUnit(), () -> {
            IdempotentStorage secondIdempotentStorage = null;
            if (StringUtils.hasText(idempotentProperties.getSecondLevelType())) {
                secondIdempotentStorage = idempotentStorageFactory.getIdempotentStorage(IdempotentStorageTypeEnum.valueOf(idempotentProperties.getSecondLevelType()));
            }

            IdempotentStorage firstIdempotentStorage = idempotentStorageFactory.getIdempotentStorage(IdempotentStorageTypeEnum.valueOf(idempotentProperties.getFirstLevelType()));


            if (request.getReadWriteType() == ReadWriteTypeEnum.ORDER) {
                orderExecute(firstIdempotentStorage, secondIdempotentStorage, request, execute, fail);
            }

            if (request.getReadWriteType() == ReadWriteTypeEnum.PARALLEL) {
                parallelExecute(firstIdempotentStorage, secondIdempotentStorage, request, execute, fail);
            }

            fail.run();
        }, fail);
    }
}