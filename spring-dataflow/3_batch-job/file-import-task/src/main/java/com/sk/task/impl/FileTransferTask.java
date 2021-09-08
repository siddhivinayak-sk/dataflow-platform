package com.sk.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.sk.service.FileService;
import com.sk.task.ExecutorTask;

//@Component
public class FileTransferTask implements ExecutorTask {

	private static final Logger LOG = LoggerFactory.getLogger(FileTransferTask.class);

	@Autowired
	FileService fileService;

	@Override
	public void execute(String accessToken) {
		LOG.info("FileTransferTask - execute");
		fileService.transferFile();		
	}

}
