package com.cxytiandi.kitty.async;

import com.jd.platform.async.callback.IWorker;
import com.jd.platform.async.executor.Async;
import com.jd.platform.async.worker.ResultState;
import com.jd.platform.async.worker.WorkResult;
import com.jd.platform.async.wrapper.WorkerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AsyncTemplate {

    private final static Logger log = LoggerFactory.getLogger(AsyncTemplate.class);

    private final static int DEFAULT_TIMEOUT = 3000;

    public static <T, R> Map<T, R> call(List<T> ids, Function<T, R> businessLogic, Function<? super R, ? extends T> keyMapper) {
        return call(ids, businessLogic, keyMapper, DEFAULT_TIMEOUT, Async.COMMON_POOL);
    }

    public static <T, R> Map<T, R> call(List<T> ids, Function<T, R> businessLogic, Function<? super R, ? extends T> keyMapper, ThreadPoolExecutor pool) {
        return call(ids, businessLogic, keyMapper, DEFAULT_TIMEOUT, pool);
    }

    public static <T, R> Map<T, R> call(List<T> ids, Function<T, R> businessLogic, Function<? super R, ? extends T> keyMapper, long timeout) {
        return call(ids, businessLogic, keyMapper, timeout, Async.COMMON_POOL);
    }

    public static <T, R> Map<T, R> call(List<T> ids, Function<T, R> businessLogic, Function<? super R, ? extends T> keyMapper, long timeout, ThreadPoolExecutor pool) {
        List<WorkerWrapper<T, R>> workerWrappers = new ArrayList<>();
        for (T id : ids) {
            IWorker<T, R> iWorker = new IWorker<T, R>() {
                @Override
                public R action(Object object, Map allWrappers) {
                    R result = businessLogic.apply(id);
                    return result;
                }
            };
            WorkerWrapper<T, R> workerWrapper = new WorkerWrapper.Builder<T, R>().worker(iWorker).build();
            workerWrappers.add(workerWrapper);
        }

        try {
            Async.beginWork(timeout, pool, workerWrappers.toArray(new WorkerWrapper[workerWrappers.size()]));
            Map<T, R> collect = workerWrappers.stream().map(
                    wr -> wr.getWorkResult().getResult()).filter(Objects::nonNull).collect(Collectors.toMap(keyMapper, Function.identity(), (k1, k2) -> k1));
            return collect;
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <R> void call(List<AsyncCall> params, Function<AsyncCall, R> businessLogic) {
        call(params, businessLogic, DEFAULT_TIMEOUT, Async.COMMON_POOL);
    }

    public static <R> void call(List<AsyncCall> params, Function<AsyncCall, R> businessLogic, ThreadPoolExecutor pool) {
        call(params, businessLogic, DEFAULT_TIMEOUT, pool);
    }

    public static <R> void call(List<AsyncCall> params, Function<AsyncCall, R> businessLogic, long timeout, ThreadPoolExecutor pool) {
        Map<String, String> workerWrapperRefParams = new HashMap<>();
        List<WorkerWrapper<R, R>> workerWrappers = new ArrayList<>();
        for (AsyncCall param : params) {
            IWorker<R, R> iWorker = new IWorker<R, R>() {
                @Override
                public R action(Object object, Map allWrappers) {
                    R result = businessLogic.apply(param);
                    return result;
                }
            };
            WorkerWrapper<R, R> workerWrapper = new WorkerWrapper.Builder<R, R>().worker(iWorker).build();
            workerWrappers.add(workerWrapper);
            workerWrapperRefParams.put(workerWrapper.getId(), param.getTaskId());
        }

        try {
            Async.beginWork(timeout, pool, workerWrappers.toArray(new WorkerWrapper[workerWrappers.size()]));
            workerWrappers.stream().forEach(w -> {
                String paramId = workerWrapperRefParams.get(w.getId());
                params.forEach(p -> {
                    if (p.getTaskId().equals(paramId)) {
                        WorkResult<R> workResult = w.getWorkResult();
                        p.setResult(w.getWorkResult().getResult());
                        if (ResultState.SUCCESS != workResult.getResultState()) {
                            log.error(w.getWorkResult().getResultState().name(), w.getWorkResult().getEx());
                        }
                    }
                });
            });
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
