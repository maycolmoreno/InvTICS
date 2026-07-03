package com.uisrael.consumogestionactivosapi.config;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import com.uisrael.consumogestionactivosapi.security.SesionUsuario;

@Configuration
public class WebClientConfig {

	private static final Logger logger = LoggerFactory.getLogger(WebClientConfig.class);

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
	JdkClientHttpRequestFactory httpRequestFactory() {
		HttpClient httpClient = HttpClient.newBuilder()
				.connectTimeout(Duration.ofSeconds(connectTimeoutSeconds))
				.build();
		JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
		factory.setReadTimeout(Duration.ofSeconds(readTimeoutSeconds));
		return factory;
	}

	@Primary
	@Bean
	RestClient restClient(JdkClientHttpRequestFactory httpRequestFactory,
			@Qualifier("publicRestClient") RestClient publicRestClient) {
		return RestClient.builder()
			.baseUrl(apiBaseUrl)
			.requestFactory(httpRequestFactory)
			.requestInterceptor((request, body, execution) -> {
				if (sesionUsuario.isAutenticado()) {
					refrescarTokenSiNecesario(publicRestClient);
					String authHeader = sesionUsuario.getAuthorizationHeader();
					if (authHeader != null) {
						request.getHeaders().set("Authorization", authHeader);
					}
				}
				return execution.execute(request, body);
			})
			.build();
	}

	@Bean("publicRestClient")
	RestClient publicRestClient(JdkClientHttpRequestFactory httpRequestFactory) {
		return RestClient.builder()
			.baseUrl(apiBaseUrl)
			.requestFactory(httpRequestFactory)
			.build();
	}

	/**
	 * Renueva el access token contra /auth/refresh cuando está por vencer.
	 * Si el refresh falla, la petición continúa con el token actual y el
	 * 401 resultante lo maneja el GlobalExceptionHandler (redirige a login).
	 */
	private void refrescarTokenSiNecesario(RestClient publicRestClient) {
		if (!sesionUsuario.tokenPorVencer() || sesionUsuario.getRefreshToken() == null) {
			return;
		}
		synchronized (sesionUsuario) {
			if (!sesionUsuario.tokenPorVencer()) {
				return;
			}
			try {
				Map<String, Object> respuesta = publicRestClient.post()
						.uri("/auth/refresh")
						.body(Map.of("refreshToken", sesionUsuario.getRefreshToken()))
						.retrieve()
						.body(new ParameterizedTypeReference<Map<String, Object>>() {
						});
				if (respuesta != null && respuesta.get("accessToken") instanceof String nuevoAccess) {
					String nuevoRefresh = respuesta.get("refreshToken") instanceof String r ? r : null;
					long expiresIn = respuesta.get("expiresIn") instanceof Number n ? n.longValue() : 900000L;
					sesionUsuario.actualizarTokens(nuevoAccess, nuevoRefresh, expiresIn);
					logger.debug("Access token renovado para {}", sesionUsuario.getCorreo());
				}
			} catch (Exception e) {
				logger.warn("No se pudo renovar el access token: {}", e.getMessage());
			}
		}
	}

}
