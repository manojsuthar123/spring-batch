package com.springbatch.controller;

import io.micrometer.core.annotation.Timed;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RestController
public class JobController {

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job job;

    @Timed(value = "batchjob.time", description = "Time taken to return JobController#startJob")
    @GetMapping("/start-job")
    public ResponseEntity<Map<String, Object>> startJob() {

        //TODO add scheduler based job execution

        Map<String, Object> responseMap = new HashMap<>();

        try {

            //set job parameters
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis()).toJobParameters();
            JobExecution jobExecution = jobLauncher.run(job, jobParameters);


            if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                responseMap.put("Job_id", jobExecution.getJobId());
                responseMap.put("Job_name", jobExecution.getJobInstance().getJobName());
                responseMap.put("Job_parameters", jobExecution.getJobParameters());
                responseMap.put("Job_status", jobExecution.getStatus().name());
                responseMap.put("Job_running", jobExecution.isRunning());
                responseMap.put("Job_create_time", jobExecution.getCreateTime());
                responseMap.put("Job_started_at", jobExecution.getStartTime());
                responseMap.put("Job_completed_at", jobExecution.getEndTime());
                responseMap.put("Job_exit", jobExecution.getExitStatus().getExitDescription());
                jobExecution.getStepExecutions().forEach(stepExecution -> {
                    responseMap.put("Step_id", stepExecution.getId());
                    responseMap.put("Step_name", stepExecution.getStepName());
                    responseMap.put("Step_start_at", stepExecution.getStartTime());
                    responseMap.put("Step_read_count", stepExecution.getReadCount());
                    responseMap.put("Step_write_count", stepExecution.getWriteCount());
                    responseMap.put("Step_skip_count", stepExecution.getSkipCount());
                });
                return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.OK);
            } else {
                List<Throwable> allFailureExceptions = jobExecution.getAllFailureExceptions();
                allFailureExceptions.forEach(throwable -> {
                    System.out.println("Job errors: " + throwable.getLocalizedMessage());
                });

                responseMap.put("status", "failed");
                responseMap.put("message", "Job Failed");
                return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.EXPECTATION_FAILED);
            }

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            responseMap.put("status", "failed");
            responseMap.put("message", "Job Failed");
            return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.EXPECTATION_FAILED);
        }

    }
}
