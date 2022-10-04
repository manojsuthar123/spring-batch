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
    public ResponseEntity<Map<String, String>> startJob() {

        //TODO add scheduler based job execution

        Map<String, String> responseMap = new HashMap<>();

        try {

            //set job parameters
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis()).toJobParameters();
            JobExecution execution = jobLauncher.run(job, jobParameters);


            if (execution.getStatus() == BatchStatus.COMPLETED) {
                responseMap.put("status", "success");
                responseMap.put("message", "Job Completed");
                return new ResponseEntity<Map<String, String>>(responseMap, HttpStatus.OK);
            } else {
                List<Throwable> allFailureExceptions = execution.getAllFailureExceptions();
                allFailureExceptions.forEach(throwable -> {
                    System.out.println("error123: "+throwable.getLocalizedMessage());
                });

                responseMap.put("status", "failed");
                responseMap.put("message", "Job Failed");
                return new ResponseEntity<Map<String, String>>(responseMap, HttpStatus.EXPECTATION_FAILED);
            }

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            responseMap.put("status", "failed");
            responseMap.put("message", "Job Failed");
            return new ResponseEntity<Map<String, String>>(responseMap, HttpStatus.EXPECTATION_FAILED);
        }

    }
}
