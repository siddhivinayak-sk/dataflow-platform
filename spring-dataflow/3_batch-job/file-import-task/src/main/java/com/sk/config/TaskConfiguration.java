package com.sk.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import com.sk.task.ExecutorTask;

//@Configuration
//@EnableTask
public class TaskConfiguration {

	@Autowired
	AuthManager authManager;

	@Autowired
	ExecutorTask executorTask;

	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			String accessToken = authManager.getAccessToken();
			if (null != accessToken) {
				executorTask.execute(accessToken);
			}
			else {
				System.out.println(false);
			}
		};
	}

}