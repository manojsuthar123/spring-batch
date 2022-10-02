package com.springbatch.listener;

import org.springframework.batch.core.StepListener;

public interface ItemProcessListener<T, S> extends StepListener {

    void beforeProcess(T item);
    void afterProcess(T item, S result);
    void onProcessError(T item, Exception e);

}
