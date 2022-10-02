package com.springbatch.listener;

import org.springframework.batch.core.StepListener;
import org.springframework.batch.core.scope.context.ChunkContext;

public interface ChunkListener extends StepListener {

    void beforeChunk(ChunkContext context);
    void afterChunk(ChunkContext context);
    void afterChunkError(ChunkContext context);

}