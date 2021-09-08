package com.sk.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sk.model.Account;
import com.sk.model.AccountDetail;


@Configuration
@EnableTask
@EnableBatchProcessing
public class ImportBatchConfiguration {
	
	private static final Logger LOG = LoggerFactory.getLogger(ImportBatchConfiguration.class);

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Value("${input.file.path}")
	private String inputResource;
	
	@Value("${output.file.path}")
	private String outputResource;

	@Value("${processed.file.path}")
	private String moveInputResoruce;
	
	
	@Bean
	public Job job1(ItemReader<Account> reader, ItemProcessor<Account, AccountDetail> itemProcessor, ItemWriter<AccountDetail> writer, JobExecutionListener jobExecutionListener) {
		Step step = stepBuilderFactory.get("ImportProcessing")
				.<Account, AccountDetail>chunk(1)
				.reader(reader)
				.processor(itemProcessor)
				.writer(writer)
				.build();

		return jobBuilderFactory.get("ImportJob")
				.incrementer(new RunIdIncrementer())
				.start(step)
				.listener(jobExecutionListener)
				.build();
	}
	
	@Bean
	@Primary
	public JobExecutionListener jobExectionListener() {
		return new JobExecutionListener() {

			@Override
			public void beforeJob(JobExecution jobExecution) {
				//do nothing
			}

			@Override
			public void afterJob(JobExecution jobExecution) {
				File inputResourceFile = new File(inputResource);
				if(inputResourceFile.exists()) {
					File inputFile;
					try {
						inputFile = inputResourceFile;
						Files.move(Paths.get(inputFile.getAbsolutePath()), Paths.get(moveInputResoruce+File.separator+inputFile.getName()+System.currentTimeMillis()), StandardCopyOption.REPLACE_EXISTING);
						System.out.println("true");
					} catch (IOException e) {
						LOG.error("Exception", e);
					}
				}
			}
		};
	}
	
	@Bean
	@Primary
	public FlatFileItemReader<Account> csvItemReader() {
	    FlatFileItemReader <Account> reader = new FlatFileItemReader<>();
	    reader.setResource(new FileSystemResource(inputResource));
	    reader.setStrict(false);
	    reader.setLineMapper(new DefaultLineMapper <Account> () {
	        {
	            setLineTokenizer(new DelimitedLineTokenizer() {
	                {
	                    setNames(new String[] {
	                        "accountNo",
	                        "customerId",
	                        "bankCode",
	                        "branchCode"
	                    });
	                }
	            });
	            setFieldSetMapper(new BeanWrapperFieldSetMapper <Account> () {
	                {
	                    setTargetType(Account.class);
	                }
	            });
	        }
	    });
	    return reader;
	}	
	
	
	@Bean
	@Primary
	public StaxEventItemWriter<AccountDetail> itemStaxEventWriter() throws Exception {
        Resource exportFileResource = new FileSystemResource(outputResource);
        XStreamMarshaller studentMarshaller = new XStreamMarshaller();
        studentMarshaller.setAliases(Collections.singletonMap(
                "account",
                AccountDetail.class
        ));
        return new StaxEventItemWriterBuilder<AccountDetail>()
                .name("accountWriter")
                .resource(exportFileResource)
                .marshaller(studentMarshaller)
                .rootTagName("accounts")
                .build();

		/*
		Map<String, String> aliases = new HashMap<>();
		aliases.put("account","com.sk.model.AccountDetail");
		aliases.put("accountNo","java.lang.String");
		aliases.put("customerId","java.lang.String");
		aliases.put("bankCode","java.lang.String");
		aliases.put("branchCode","java.lang.String");
		aliases.put("countryCode","java.lang.String");
		aliases.put("balance","java.lang.Double");
		XStreamMarshaller marshaller = new XStreamMarshaller();
		marshaller.setAliases(aliases);
		StaxEventItemWriter<AccountDetail> staxItemWriter =
			new StaxEventItemWriterBuilder<AccountDetail>()
						.name("accountDetailWriter")
						.marshaller(marshaller)
						.resource(new FileSystemResource(outputResource))
						.rootTagName("accounts")
						.overwriteOutput(true)
						.build();
		staxItemWriter.afterPropertiesSet();

		ExecutionContext executionContext = new ExecutionContext();
		staxItemWriter.open(executionContext);
		return staxItemWriter;
		*/
	}
	
	public JsonItemReader<Account> jsonItemReader() {
		ObjectMapper objectMapper = new ObjectMapper();
		JacksonJsonObjectReader<Account> jsonObjectReader = new JacksonJsonObjectReader<>(Account.class);
		jsonObjectReader.setMapper(objectMapper);
		return new JsonItemReaderBuilder<Account>()
				.jsonObjectReader(jsonObjectReader)
				.resource(new FileSystemResource(inputResource))
				.name("UsageJsonItemReader")
				.build();
	}
	
	public ItemWriter<AccountDetail> jdbcItemWriter(DataSource dataSource) {
		JdbcBatchItemWriter<AccountDetail> writer = new JdbcBatchItemWriterBuilder<AccountDetail>()
			.beanMapped()
			.dataSource(dataSource)
			.sql("INSERT INTO BILL_STATEMENTS (id, first_name, last_name, minutes, data_usage,bill_amount) VALUES (:id, :firstName, :lastName, :minutes, :dataUsage, :billAmount)")
			.build();
		return writer;
	}

	@Bean
	@Primary
	ItemProcessor<Account, AccountDetail> itemProcessor() {
		return new ImportBatchProcessor();
	}

}
