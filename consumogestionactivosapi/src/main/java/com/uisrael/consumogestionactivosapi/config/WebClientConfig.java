package com.uisrael.consumogestionactivosapi.config;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

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
	WebClient WebClient(WebClient.Builder builder) {
		return builder
			.baseUrl(apiBaseUrl)
			.filter((request, next) -> {
				if (sesionUsuario.isAutenticado()) {
					String credenciales = sesionUsuario.getCorreo() + ":" + sesionUsuario.getContrasena();
					String credencialesBase64 = Base64.getEncoder().encodeToString(credenciales.getBytes());
					request = org.springframework.web.reactive.function.client.ClientRequest
							.from(request)
							.header("Authorization", "Basic " + credencialesBase64)
							.build();
				}
				return next.exchange(request);
			})
			.build();
	}

}

