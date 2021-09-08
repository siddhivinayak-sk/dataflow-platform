package com.sk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

import com.sk.model.ApplicationProps;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProps.class)
@ComponentScan(value = "com.sk.*")
public class Application {
 
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
