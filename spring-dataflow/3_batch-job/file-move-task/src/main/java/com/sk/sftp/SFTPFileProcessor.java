package com.sk.sftp;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

@Lazy
@Component
public class SFTPFileProcessor extends AbstractFileProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(SFTPFileProcessor.class);

	@Value("${sftp.username:}")
	private String userId;

	@Value("${sftp.password:}")
	private String password;

	@Value("${sftp.remoteDir:}")
	private String remoteDirectory;

	@Value("${sftp.serverhost:}")
	private String serverAddress;

	private Channel channel;

	private Session session;
	
	private String mode;

	//@PostConstruct
	public void initChannel() {
	 channel = createChannel();
	}

	public void initChannel(String mode) {
		this.mode = mode;
		if(mode.equalsIgnoreCase("sftp")) {
			channel = createChannel();
		}
	}
	
	public SFTPFileProcessor() {

	}

	public SFTPFileProcessor(String userId, String password, String serverAddress, String remoteDirectory) {
		super();
		this.userId = userId;
		this.password = password;
		this.serverAddress = serverAddress;
		this.remoteDirectory = remoteDirectory;
	}

	public SFTPFileProcessor(Properties props) {

		this.serverAddress = props.getProperty("serverAddress").trim();
		this.userId = props.getProperty("userId").trim();
		this.password = props.getProperty("password").trim();
		this.remoteDirectory = props.getProperty("remoteDirectory").trim();
		this.localDirectory = props.getProperty("localDirectory").trim();
	}

	public Channel createChannel() {
		JSch jSchConnection = new JSch();
		try {
			session = jSchConnection.getSession(userId, serverAddress);
			session.setPassword(password);
			Properties properties = new Properties();
			properties.setProperty("StrictHostKeyChecking", "no");
			session.setConfig(properties);
			session.setTimeout(10000);
			session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
			session.connect(20000);
			channel = session.openChannel("sftp");
			channel.connect();
			LOG.info("Connected with SFTP");
		} catch (JSchException e) {
			LOG.error("Something went wrong!! Channel creation failed" + e.getMessage(), e);
		}
		return channel;
	}

	public InputStream getServerStream(String pathToInput) throws SftpException {
		ChannelSftp channelSftp = (ChannelSftp) channel;
		return channelSftp.get(pathToInput);
	}

	public void closeConnection() {
		if(mode.equalsIgnoreCase("sftp")) {
			ChannelSftp channelSftp = (ChannelSftp) channel;
			channelSftp.disconnect();
			session.disconnect();
		}
	}

	public byte[] getFile(String remoteDir, String fileName) throws SftpException, IOException {
		ChannelSftp channelSftp = (ChannelSftp) channel;
		// channelSftp.cd("/" + remoteDir.replace("\\", "/"));
		InputStream is = channelSftp.get(fileName);
		return IOUtils.toByteArray(is);
	}

	public String getDATFileAndMove(String remoteDir, String destDir) throws SftpException {
		ChannelSftp channelSftp = (ChannelSftp) channel;
		// channelSftp.cd("/" + remoteDir.replace("\\", "/"));
		String fileName = "";
		Vector<LsEntry> filesList = channelSftp.ls(remoteDir);
		if (filesList == null || filesList.isEmpty()) {
			return fileName;
		}
		for (LsEntry file : filesList) {
			if (!".".equals(file.getFilename()) && !"..".equals(file.getFilename())) {
				if (file.getFilename().endsWith(".DAT")) {
					fileName = file.getFilename();
				}
				channelSftp.rename(remoteDir + "/" + file.getFilename(), destDir + "/" + file.getFilename());
			}
		}
		return fileName;
	}

	public String getDATFileName(String remoteDir) throws SftpException {
		ChannelSftp channelSftp = (ChannelSftp) channel;
		channelSftp.cd(remoteDir);
		String fileName = "";
		Vector<LsEntry> filesList = channelSftp.ls(remoteDir);
		if (filesList == null || filesList.isEmpty()) {
			return fileName;
		}
		for (LsEntry file : filesList) {
			if (!".".equals(file.getFilename()) && !"..".equals(file.getFilename())) {
				if (file.getFilename().endsWith(".DAT")) {
					fileName = file.getFilename();
				}
			}
		}
		return fileName;
	}

	public List<String> readFromFile(String path) throws IOException, SftpException {
		ChannelSftp channelSftp = (ChannelSftp) channel;

		List<String> fileContent = new ArrayList<>();
		try (InputStream stream = channelSftp.get(path);
				InputStreamReader isr = new InputStreamReader(stream);
				BufferedReader br = new BufferedReader(isr)) {

			String line;
			while ((line = br.readLine()) != null) {
				fileContent.add(line);
			}

		} catch (Exception e) {
			LOG.error("Unexpected exception readFromFile on sftp: " + e.getMessage());
		}
		return fileContent;
	}

	public boolean isFileExist(String sourceFile) {
		ChannelSftp channelSftp = (ChannelSftp) channel;
		Vector res = null;
		try {
			res = channelSftp.ls(sourceFile);
		} catch (SftpException e) {
			LOG.error("Unexpected exception during ls files on sftp: [{}:{}]", e.id, e.getMessage());
			return false;
		}
		return res != null && !res.isEmpty();
	}

	public List<String> getAllFiles(String remoteDir, String regex) throws SftpException {
		List<String> files = new ArrayList<>();
		if(mode.equalsIgnoreCase("sftp")) {
			ChannelSftp channelSftp = (ChannelSftp) channel;
			// channelSftp.cd("/" + remoteDir.replace("\\", "/"));
			Vector<LsEntry> filesList = channelSftp.ls(remoteDir);
			if (filesList == null || filesList.isEmpty()) {
				return files;
			}
			for (LsEntry file : filesList) {
				if (!".".equals(file.getFilename()) && !"..".equals(file.getFilename()) && file.getFilename().matches(regex)) {
					files.add(file.getFilename().toString());
				}
			}
		}
		else {
			for(File file:new File(remoteDir).listFiles()) {
				if(file.getName().matches(regex)) {
					files.add(file.getName());
				}
			}
		}
		return files;
	}

	public List<File> getAll(String remoteDir) throws SftpException {

		ChannelSftp channelSftp = (ChannelSftp) channel;
		// channelSftp.cd("/" + remoteDir.replace("\\", "/"));

		List<File> files = new ArrayList<>();
		Vector<LsEntry> filesList = channelSftp.ls(remoteDir);
		if (filesList == null || filesList.isEmpty()) {
			return files;
		}

		// FileManager fileManager = new FileManager(rootPath,
		// FileSeparator.BACKWARD_SLASH);

		List<String> filess = new ArrayList<>();
		for (LsEntry file : filesList) {
			if (!".".equals(file.getFilename()) && !"..".equals(file.getFilename())) {
				filess.add(file.getFilename());

			}
		}

		return files;
	}

	public void rename(String remoteDir, String sourceFileName, String targetFileName) throws SftpException {
		ChannelSftp channelSftp = (ChannelSftp) channel;
		// channelSftp.cd("/" + remoteDir.replace("\\", "/"));
		channelSftp.rename(remoteDir + "/" + sourceFileName, remoteDir + "/" + targetFileName);
	}

	public String writeToFile(String path, String fileName, List<String> roles) throws IOException, SftpException {
		ChannelSftp channelSftp = (ChannelSftp) channel;
		StringBuilder createPath = new StringBuilder();
		String[] folders = path.split("/");
		for (String folder : folders) {
			if (folder.length() > 0 && !folder.contains(".")) {
				try {
					createPath.append("/");
					createPath.append(folder);
					channelSftp.cd(createPath.toString());
				} catch (SftpException e) {
					channelSftp.mkdir(createPath.toString());
					channelSftp.cd(createPath.toString());
				}
			}
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (String line : roles) {
			baos.write(line.getBytes());
		}
		byte[] bytes = baos.toByteArray();
		InputStream in = new ByteArrayInputStream(bytes);
		channelSftp.put(in, fileName);
		return path;
	}

	public InputStream readFile(String path) throws SftpException {
		ChannelSftp channelSftp = (ChannelSftp) channel;

		return channelSftp.get(path);
	}

	public String writeToFileForFects(String path, String fileName, byte[] fileContent)
			throws IOException, SftpException {
		LOG.info("Inside the SFTPFileProcessor:: writeToFileForFects method");

		ChannelSftp channelSftp = (ChannelSftp) channel;
		StringBuilder createPath = new StringBuilder();

		String[] folders = path.split("/");
		for (String folder : folders) {
			if (folder.length() > 0 && !folder.contains(".")) {
				try {
					createPath.append("/");
					createPath.append(folder);

					channelSftp.cd(createPath.toString());
				} catch (SftpException e) {
					channelSftp.mkdir(createPath.toString());
					channelSftp.cd(createPath.toString());
				}
			}
		}

		InputStream in = new ByteArrayInputStream(fileContent);
		channelSftp.put(in, fileName);
		return path;
	}

	public void moveFiles(String remoteDir, String destDir) throws SftpException {
		ChannelSftp channelSftp = (ChannelSftp) channel;
		//channelSftp.cd("/" + remoteDir.replace("\\", "/"));
		//channelSftp.rename(remoteDir, destDir);
		
		
		Vector<ChannelSftp.LsEntry> list = channelSftp.ls(remoteDir);
	    for(ChannelSftp.LsEntry entry : list) {
	         System.out.println(entry.getFilename()); 
	         channelSftp.rename(remoteDir+"/"+entry.getFilename(), destDir+"/"+entry.getFilename());
	    }
	}

	public void moveFiles(String remoteDir, String sourceFileName, String targetDir, String targetFileName, String associatedRegex)
			throws SftpException, IOException {
		if(mode.equalsIgnoreCase("sftp")) {
			ChannelSftp channelSftp = (ChannelSftp) channel;
			channelSftp.rename(remoteDir + "/" + sourceFileName, targetDir + "/" + targetFileName);
		}
		else {
			String fileNameWithoutExt = sourceFileName.substring(0, sourceFileName.lastIndexOf('.'));
			if(null != associatedRegex) {
				for(String ext:associatedRegex.split(",")) {
					Path source = Paths.get(remoteDir, fileNameWithoutExt + ext);
					Path target = Paths.get(targetDir, fileNameWithoutExt + ext);
					if(Files.exists(source, LinkOption.NOFOLLOW_LINKS)) {
						Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
					}
				}
			}
			else {
				Path source = Paths.get(remoteDir, sourceFileName);
				Path target = Paths.get(targetDir, sourceFileName);
				if(Files.exists(source, LinkOption.NOFOLLOW_LINKS)) {
					Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
				}
			}
		}
	}

	public String fileSFTP(String sourceFileName, String targetDir, String targetFileName) throws SftpException {
		LOG.info("Inside the SFTPFileProcessor:: fileSFTP method");
		ChannelSftp channelSftp = (ChannelSftp) channel;
		StringBuilder createPath = new StringBuilder();
		String[] folders = targetDir.split("/");

		for (String folder : folders) {
			if (folder.length() > 0 && !folder.contains(".")) {
				try {
					createPath.append("/");
					createPath.append(folder);
					channelSftp.cd(createPath.toString());

				} catch (SftpException e) {
					channelSftp.mkdir(createPath.toString());
					channelSftp.cd(createPath.toString());
				}
			}
		}

		channelSftp.put(sourceFileName, targetDir + "/" + targetFileName);
		return targetDir;
	}

	public String createDirectoryOnSftp(String destDir) throws SftpException {
		if(mode.equalsIgnoreCase("sftp")) {
			ChannelSftp channelSftp = (ChannelSftp) channel;
			StringBuilder createPath = new StringBuilder();
			String[] folders = destDir.split("/");

			for (String folder : folders) {
				if (folder.length() > 0 && !folder.contains(".")) {
					try {
						createPath.append("/");
						createPath.append(folder);
						channelSftp.cd(createPath.toString());

					} catch (SftpException e) {
						channelSftp.mkdir(createPath.toString());
						channelSftp.cd(createPath.toString());
					}
				}
			}
		}
		else {
			File file = new File(destDir);
			if(!file.exists()) {
				file.mkdirs();
			}
		}
		return destDir;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

}
