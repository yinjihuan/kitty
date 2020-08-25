package com.cxytiandi.kitty.lock.idempotent;

import com.cxytiandi.kitty.lock.DistributedLock;
import com.cxytiandi.kitty.lock.idempotent.enums.ReadWriteTypeEnum;
import com.cxytiandi.kitty.lock.idempotent.properties.IdempotentProperties;
import com.cxytiandi.kitty.lock.idempotent.request.IdempotentRequest;
import com.cxytiandi.kitty.lock.idempotent.storage.IdempotentStorage;
import com.cxytiandi.kitty.lock.idempotent.storage.IdempotentStorageFactory;
import com.cxytiandi.kitty.lock.idempotent.storage.IdempotentStorageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
    private RedissonClient redissonClient;

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
        return execute(key, lockExpireTime, firstLevelExpireTime, secondLevelExpireTime, timeUnit, ReadWriteTypeEnum.ORDER, execute, fail);
    }

    private <T> T orderExecute(IdempotentStorage firstIdempotentStorage, IdempotentStorage secondIdempotentStorage, IdempotentRequest request, Supplier<T> execute, Supplier<T> fail) {
        String firstValue = firstIdempotentStorage.getValue(request.getKey());
        String secondValue = null;
        if (secondIdempotentStorage != null) {
            secondValue = secondIdempotentStorage.getValue(request.getKey());
        }

        // 一级和二级存储中都没有数据，表示可以继续执行
        if (!StringUtils.hasText(firstValue) && !StringUtils.hasText(secondValue)) {
            T executeResult = execute.get();
            firstIdempotentStorage.setValue(request.getKey(), idempotentDefaultValue, request.getFirstLevelExpireTime(), request.getTimeUnit());
            if (secondIdempotentStorage != null) {
                secondIdempotentStorage.setValue(request.getKey(), idempotentDefaultValue, request.getSecondLevelExpireTime(), request.getTimeUnit());
            }
            return executeResult;
        }

        // 不能继续往下执行
        return fail.get();
    }

    private <T> T parallelExecute(IdempotentStorage firstIdempotentStorage, IdempotentStorage secondIdempotentStorage, IdempotentRequest request, Supplier<T> execute, Supplier<T> fail) {
        List<String> results = getParallelExecuteResults(request.getKey(), firstIdempotentStorage, secondIdempotentStorage);
        if (results.stream().filter(StringUtils::hasText).count() == 0) {
            T executeResult = execute.get();
            parallelWriteResults(firstIdempotentStorage, secondIdempotentStorage, request);
            return executeResult;
        }
        return fail.get();
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
        } catch (InterruptedException e) {
            log.error("并行写异常", e);
        } catch (ExecutionException e) {
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
        } catch (InterruptedException e) {
            log.error("并行读异常", e);
        } catch (ExecutionException e) {
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
        // todo: 二级存储待实现
        distributedLock.lock(key + lockSuffix, lockExpireTime, timeUnit, () -> {
            RBucket<String> bucket = redissonClient.getBucket(key);
            if (bucket != null && bucket.get() != null) {
                fail.run();
            } else {
                execute.run();
                bucket.set(idempotentDefaultValue, firstLevelExpireTime, timeUnit);
            }
        }, fail);
    }

    @Override
    public void execute(String key, int lockExpireTime, int firstLevelExpireTime, int secondLevelExpireTime, TimeUnit timeUnit, Runnable execute, Runnable fail) {
        execute(key, lockExpireTime, firstLevelExpireTime, secondLevelExpireTime, timeUnit, ReadWriteTypeEnum.ORDER, execute, fail);
    }

    @Override
    public void execute(IdempotentRequest request, Runnable execute, Runnable fail) {
        execute(request.getKey(), request.getLockExpireTime(), request.getFirstLevelExpireTime(), request.getSecondLevelExpireTime(), request.getTimeUnit(), request.getReadWriteType(), execute, fail);
    }
}