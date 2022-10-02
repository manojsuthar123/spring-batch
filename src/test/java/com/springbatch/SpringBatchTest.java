package com.springbatch;

import com.springbatch.config.BatchConfiguration;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.test.AssertFile;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@org.springframework.batch.test.context.SpringBatchTest
@EnableAutoConfiguration
@ContextConfiguration(classes = {BatchConfiguration.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class SpringBatchTest {

    private static final String TEST_OUTPUT = "src/test/resources/output/outputData.csv";

    private static final String EXPECTED_OUTPUT = "src/test/resources/output/outputData.csv";

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Test
    public void testJob() throws Exception {


        JobExecution jobExecution = jobLauncherTestUtils.launchStep("step1");


        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
    }

    private JobParameters defaultJobParameters() {
        JobParametersBuilder paramsBuilder = new JobParametersBuilder();
        paramsBuilder.addLong("startAt", System.currentTimeMillis());
        return paramsBuilder.toJobParameters();
    }

    @Test
    public void givenReferenceOutput_whenStep1Executed_thenSuccess() throws Exception {
        // given
        FileSystemResource expectedResult = new FileSystemResource(EXPECTED_OUTPUT);
        FileSystemResource actualResult = new FileSystemResource(TEST_OUTPUT);

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep(
                "step1", defaultJobParameters());
        Collection<StepExecution> actualStepExecutions = jobExecution.getStepExecutions();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        // then
        assertEquals(actualStepExecutions.size(), 1);
        assertEquals(actualJobExitStatus.getExitCode(), "COMPLETED");
        AssertFile.assertFileEquals(expectedResult, actualResult);
    }

    @After
    public void cleanUp() {
        jobRepositoryTestUtils.removeJobExecutions();
    }
}
