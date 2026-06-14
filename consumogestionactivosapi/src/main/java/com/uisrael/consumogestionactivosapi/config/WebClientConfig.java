package com.uisrael.consumogestionactivosapi.config;

import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import com.uisrael.consumogestionactivosapi.security.SesionUsuario;

@Configuration
public class WebClientConfig {

	private final SesionUsuario sesionUsuario;

	@Value("${api.base-url}")
	private String apiBaseUrl;

	@Value("${api.connect-timeout:5}")
	private int connectTimeoutSeconds;

	@Value("${api.read-timeout:30}")
	private int readTimeoutSeconds;

	public WebClientConfig(SesionUsuario sesionUsuario) {
		this.sesionUsuario = sesionUsuario;
	}

	@Bean
	RestClient restClient() {
		HttpClient httpClient = HttpClient.newBuilder()
				.connectTimeout(Duration.ofSeconds(connectTimeoutSeconds))
				.build();

		JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
		factory.setReadTimeout(Duration.ofSeconds(readTimeoutSeconds));

		return RestClient.builder()
			.baseUrl(apiBaseUrl)
			.requestFactory(factory)
			.requestInterceptor((request, body, execution) -> {
				if (sesionUsuario.isAutenticado()) {
					String authHeader = sesionUsuario.getAuthorizationHeader();
					if (authHeader != null) {
						request.getHeaders().set("Authorization", authHeader);
					}
				}
				return execution.execute(request, body);
			})
			.build();
	}

}

