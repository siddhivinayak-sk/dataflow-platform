package com.sk.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AuthConfig {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthConfig.class);

	@Bean(name = "httpsRestTemplate")
	public RestTemplate restTemplateHttps() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		
		  TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
		  
		  SSLContext sslContext = SSLContexts
				  .custom()
				  .loadTrustMaterial(null, acceptingTrustStrategy)
				  .build();
		  SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
		  
		  PlainConnectionSocketFactory plainConnectionSocketFactory = new PlainConnectionSocketFactory();
		  
		  Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				  .register("https", sslsf)
				  .register("http", plainConnectionSocketFactory)
				  .build();
		  
		  BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager(socketFactoryRegistry);
		  
		  CloseableHttpClient httpClient = HttpClients 
		      .custom()
			  .disableCookieManagement()
			  .setSSLSocketFactory(sslsf)
			  .setConnectionManager(connectionManager)
			  .build();
		  
		  HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		  
		return new RestTemplate(requestFactory);
	}

	@Value("${custom.keystore-path:keystore.jks:}")
	private String keystorePath;

	@Value("${custom.keystore-key:secret:}")
	private String keystoreSecret;

	public HttpComponentsClientHttpRequestFactory getRequestFactory() {
        try {
        	TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        	KeyStore keyStore = KeyStore.getInstance("PKCS12");
        	keyStore.load(new FileInputStream(keystorePath), keystoreSecret.toCharArray());
        	
	        SSLContext sslContext = SSLContexts.custom()
	        		.loadKeyMaterial(new File(keystorePath), keystoreSecret.toCharArray(), keystoreSecret.toCharArray())
                    .loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();
            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(csf)
                    .build();
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);
            return requestFactory;
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException | CertificateException | IOException | UnrecoverableKeyException e) {
        	LOGGER.info("Invalid CustomRestTemplate: " + e.getMessage());
        }
        return null;
	}	
	
	
	@Bean(name = "customRestTemplate")
	public RestTemplate restTemplateCustom() {
		HttpComponentsClientHttpRequestFactory factory = getRequestFactory(); 
		return (null != factory)?new RestTemplate(factory):new RestTemplate();
	}
	
	
	@Bean(name = "httpRestTemplate")
	public RestTemplate restTemplateHttp() {
		return new RestTemplate();
	}

	
	@Bean
	@Primary
	public RestTemplate restTemplate() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		RestTemplate httpsTemplate = restTemplateHttps();
		RestTemplate httpTemplate = restTemplateHttp();
		RestTemplate customTemplate = restTemplateCustom();
		return new RestTemplate() {
			
			@Override
			public <T> ResponseEntity<T> exchange(String url, HttpMethod method,
					@Nullable HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) {
				if(url.contains(">")) {
					String actualUrl = url.split(">")[1];
					if(url.startsWith("https-custom")) {
						return customTemplate.exchange(actualUrl, method, requestEntity, responseType, uriVariables);
					}
					if(url.startsWith("https-ignore")) {
						return httpsTemplate.exchange(actualUrl, method, requestEntity, responseType, uriVariables);
					}
					if(url.startsWith("http")) {
						return httpTemplate.exchange(actualUrl, method, requestEntity, responseType, uriVariables);
					}
					else {
						return httpTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
					}
				}
				else {
					return httpTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
				}
			}
			
		};
	}
	
}
