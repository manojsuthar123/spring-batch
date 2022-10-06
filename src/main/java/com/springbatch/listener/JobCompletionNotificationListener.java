package com.springbatch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {
    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.STARTED) {
            log.info("Job Id: " + jobExecution.getJobId());
            log.info("Job name: " + jobExecution.getJobInstance().getJobName());
            log.info("Job parameters: " + jobExecution.getJobParameters());
            log.info("Job status: " + jobExecution.getStatus().name());
            log.info("Job running: " + jobExecution.isRunning());
            log.info("Job create time: " + jobExecution.getCreateTime());
            log.info("Job started at: " + jobExecution.getStartTime());
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("Job completed at: " + jobExecution.getEndTime());
            log.info("Job exit: " + jobExecution.getExitStatus().getExitDescription());
            jobExecution.getStepExecutions().forEach(stepExecution -> {
                log.info("Step Id: "+stepExecution.getId());
                log.info("Step name: "+stepExecution.getStepName());
                log.info("Step start at: "+stepExecution.getStartTime());
                log.info("Step read count: "+stepExecution.getReadCount());
                log.info("Step write count: "+stepExecution.getWriteCount());
                log.info("Step skip count: "+stepExecution.getSkipCount());
            });
        }
    }
}
