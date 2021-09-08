package com.sk.config;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;

import javax.crypto.NoSuchPaddingException;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.CustomPBEStringEncryptor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PropertyEncryptionConfig {

	private final static String ARG_NAME = "secretAppCode";
	private final static String ARG_EQ = "=";
	
	@Bean(name = "encryptorBean")
	public StringEncryptor stringEncryptor(ApplicationArguments args) throws Exception {
		CustomPBEStringEncryptor encryptor = new CustomPBEStringEncryptor();
		try {
			String[] arguments = args.getSourceArgs();
			if(null != arguments && arguments.length > 0) {
				String argument = Stream.of(arguments).filter(e -> e.contains(ARG_NAME+ARG_EQ)).findAny().orElse(ARG_NAME+ARG_EQ+" ");
				encryptor.setKey(argument.split(ARG_EQ)[1]);
			}
		} catch (InvalidKeyException | NoSuchAlgorithmException | UnsupportedEncodingException
				| NoSuchPaddingException e) {
			throw e;
		}
		return encryptor;
	}	
}
