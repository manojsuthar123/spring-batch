package com.springbatch.listener;


import org.springframework.batch.core.StepListener;

public interface ItemReadListener<T> extends StepListener {

    void beforeRead();
    void afterRead(T item);
    void onReadError(Exception ex);

}
