package com.jd.platform.async.callback;

import com.jd.platform.async.wrapper.WorkerWrapper;

import java.util.List;

/**
 * @author wuweifeng wrote on 2019-12-27
 * @version 1.0
 */
public class DefaultGroupCallback implements IGroupCallback {
    @Override
    public void success(List<WorkerWrapper> workerWrappers) {

    }

    @Override
    public void failure(List<WorkerWrapper> workerWrappers, Exception e) {

    }
}
