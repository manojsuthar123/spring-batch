package com.springbatch.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.listener.ItemListenerSupport;

import java.util.List;

public class ItemFailureLoggerListener extends ItemListenerSupport {

    private static Log logger = LogFactory.getLog("item.error");

    public void onReadError(Exception ex) {
        logger.error("Encountered error on read", ex);
    }

    public void onWriteError(Exception ex, List items) {
        logger.error("Encountered error on write", ex);
    }
}
