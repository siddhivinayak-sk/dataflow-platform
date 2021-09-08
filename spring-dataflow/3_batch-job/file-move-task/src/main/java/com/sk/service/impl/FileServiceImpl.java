package com.sk.service.impl;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.SftpException;
import com.sk.model.ApplicationProps;
import com.sk.model.Route;
import com.sk.service.FileService;
import com.sk.sftp.SFTPFileProcessor;

@Service
public class FileServiceImpl implements FileService {

	private static final Logger LOG = LoggerFactory.getLogger(FileServiceImpl.class);

	@Autowired
	SFTPFileProcessor sftpFileProcessor;

	@Autowired
	ApplicationProps applicationProps;

	public void transferFile() {

		List<Route> routeList = applicationProps.getRoutes();

		routeList.stream().forEach(route -> {
			
			sftpFileProcessor.initChannel(route.getMedium());
			
			LOG.info("===================:File movement for route: {}", route);
			try {
				if(route.isActive()) {
				List<String> fileNames = sftpFileProcessor.getAllFiles(route.getSource_dir(), route.getScan_ext());
				LOG.info("===================:Number of files to move for this route: {}", fileNames.size());

			 
				String targetPath = !fileNames.isEmpty()?sftpFileProcessor.createDirectoryOnSftp(
						new StringBuilder(route.getTarget_dir())
						.append(datePath(route.getDate_path()))
						.append("/")
						.append(route.getTarget_folder())
						.append("/")
						.toString()):null;
				 
				LOG.info("===================:Moving file from: {}  -- to: {}", route.getSource_dir(),targetPath);
				
				fileNames.stream().forEach(fileName -> {
					try {
						sftpFileProcessor.moveFiles(route.getSource_dir(), fileName, targetPath, fileName, route.getScan_ext_associated_file());
					} catch (SftpException | IOException e) {
						LOG.error("===================:Error in moving file: {}, for the route:{} exception: ", fileName,
								route, e);
					}
					});
				}
				else {
					LOG.info("===================:This route is inactive");
				}

			} catch (SftpException e) {
				LOG.error("===================:Error in moving file for route: {} exception: ", route, e);
			}
			
			sftpFileProcessor.closeConnection();
		});
		
		

	}

	private String datePath(String dateFormat) {
		DateFormat format = new SimpleDateFormat(dateFormat);
		String datepath = format.format(new Date());
		return datepath;
	}

}
