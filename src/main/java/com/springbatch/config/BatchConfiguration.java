package com.springbatch.config;

import com.springbatch.custom.PersonItemProcessor;
import com.springbatch.mapper.PersonRowMapper;
import com.springbatch.model.Person;
import io.micrometer.core.annotation.Timed;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration(exclude = {HibernateJpaAutoConfiguration.class})
public class BatchConfiguration extends DefaultBatchConfigurer {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    DataSource dataSource;

    /*@Autowired
    JobCompletionNotificationListener listener;*/

    @Override
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(null);
    }

    private Resource outputResource = new FileSystemResource("src/main/resources/output/outputData.csv");

    @Timed(value = "itemReader.time")
    //Cursor based item reader
    @Bean
    @StepScope
    public JdbcCursorItemReader<Person> itemReader() {
        return new JdbcCursorItemReaderBuilder<Person>()
                .dataSource(this.dataSource)
                .name("personReader")
                .sql("SELECT id, first_name, last_name, city FROM person")
                .rowMapper(new PersonRowMapper())
                .build();
    }

    @Bean
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<Person> writer() {
        FlatFileItemWriter<Person> writer = new FlatFileItemWriter<>();
        writer.setResource(outputResource);
        writer.setAppendAllowed(true);
        writer.setLineAggregator(new DelimitedLineAggregator<Person>() {
            {
                setDelimiter(",");
                setFieldExtractor(new BeanWrapperFieldExtractor<Person>() {
                    {
                        setNames(new String[]{"id", "firstName", "lastName", "city"});
                    }
                });
            }
        });
        return writer;
    }

    @Bean
    public Job importUserJob() {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                //.listener(listener)
                .flow(step1())
                .end()
                .build();
    }


    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<Person, Person>chunk(100)
                .reader(itemReader())
                .processor(processor())
                .writer(writer())
                //.faultTolerant()
                //.skipLimit(10)
                //.skip(Exception.class)
                //.noSkip(FileNotFoundException.class)
                //.noRollback(ValidationException.class)
                .build();
    }

}
