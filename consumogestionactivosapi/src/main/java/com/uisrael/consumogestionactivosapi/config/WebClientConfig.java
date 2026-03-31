package com.uisrael.consumogestionactivosapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import com.uisrael.consumogestionactivosapi.security.SesionUsuario;

@Configuration
public class WebClientConfig {

	private final SesionUsuario sesionUsuario;

	@Value("${api.base-url}")
	private String apiBaseUrl;

	public WebClientConfig(SesionUsuario sesionUsuario) {
		this.sesionUsuario = sesionUsuario;
	}

	@Bean
	RestClient restClient() {
		return RestClient.builder()
			.baseUrl(apiBaseUrl)
			.requestInterceptor((request, body, execution) -> {
				if (sesionUsuario.isAutenticado()) {
					request.getHeaders().setBasicAuth(
							sesionUsuario.getCorreo(), sesionUsuario.getContrasena());
				}
				return execution.execute(request, body);
			})
			.build();
	}

}

