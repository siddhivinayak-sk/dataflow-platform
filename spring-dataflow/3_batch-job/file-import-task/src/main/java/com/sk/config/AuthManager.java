package com.sk.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthManager.class);

	@Autowired
	RestTemplate restTemplate;
	
	public String getAccessToken() {
		LOGGER.info(":==================: Obtaining the access token :=======================:");
		ResponseEntity<String> response = null;
		String token = null;

		try {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		//response = restTemplate.exchange("", HttpMethod.POST, new HttpEntity<MultiValueMap<String, String>>(map, new HttpHeaders()), String.class);
		response = new ResponseEntity<String>("generated-token", HttpStatus.OK);

		if(null!=response && null != response.getBody()) {
		LOGGER.info(":==================: Auth Token Obtained Successfully :=================:");
		token = response.getBody();
		 }
		}
		catch (HttpClientErrorException e) {
			LOGGER.error(":==================: Failed to get Access Token :{}, Exception :" ,e.getResponseBodyAsString(),e);
		} catch (HttpStatusCodeException e) {
			LOGGER.error(":==================: Failed to get Access Token :{}, Exception :" ,e.getResponseBodyAsString(),e);
		} catch (RestClientException e) {
			LOGGER.error(":==================: Failed to get Access Token :" ,e);
		}
		return token;
	}

}
