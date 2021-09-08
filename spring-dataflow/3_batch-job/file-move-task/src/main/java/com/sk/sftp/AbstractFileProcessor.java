package com.sk.sftp;

public abstract class AbstractFileProcessor implements FileProcessor {

	protected String localDirectory;
	protected String remoteDirectory;

	@Override
	public void setLocalDirectory(String localDirectory) {
		this.localDirectory = localDirectory;
	}

	@Override
	public void setRemoteDirectory(String remoteDirectory) {
		this.remoteDirectory = remoteDirectory;
	}

}
