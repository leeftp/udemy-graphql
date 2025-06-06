package com.example.batch.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.example.batch.model.SampleData;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Bean
    public JdbcCursorItemReader<SampleData> reader(DataSource oracleDataSource) {
        return new JdbcCursorItemReaderBuilder<SampleData>()
                .dataSource(oracleDataSource)
                .sql("SELECT id, name, updated_at FROM sample_table WHERE updated_at > ?")
                .rowMapper(new BeanPropertyRowMapper<>(SampleData.class))
                .name("oracleReader")
                .build();
    }

    @Bean
    public org.springframework.batch.item.ItemWriter<SampleData> writer(DataSource mysqlDataSource) {
        return new JdbcBatchItemWriterBuilder<SampleData>()
                .beanMapped()
                .sql("REPLACE INTO sample_table (id, name, updated_at) VALUES (:id, :name, :updatedAt)")
                .dataSource(mysqlDataSource)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory, JdbcCursorItemReader<SampleData> reader,
                     org.springframework.batch.item.ItemWriter<SampleData> writer) {
        return stepBuilderFactory.get("step")
                .<SampleData, SampleData>chunk(100)
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get("oracleToMysqlJob")
                .incrementer(new RunIdIncrementer())
                .flow(step)
                .end()
                .build();
    }
}
