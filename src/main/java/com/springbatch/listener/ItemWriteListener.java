package com.springbatch.listener;

import org.springframework.batch.core.StepListener;

import java.util.List;

public interface ItemWriteListener<S> extends StepListener {

    void beforeWrite(List<? extends S> items);
    void afterWrite(List<? extends S> items);
    void onWriteError(Exception exception, List<? extends S> items);

}
