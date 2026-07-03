package com.uisrael.gestionactivosapi.infraestructura.seguridad;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtTokenProviderTest {

    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider();
        ReflectionTestUtils.setField(provider, "jwtSecret",
                "cGxhY2Vob2xkZXItc2VjcmV0LWtleS1mb3ItZGV2LW9ubHktMzI=");
        ReflectionTestUtils.setField(provider, "accessTokenExpirationMs", 900000L);
        ReflectionTestUtils.setField(provider, "refreshTokenExpirationMs", 604800000L);
    }

    @Test
    void generaYValidaAccessToken() {
        String token = provider.generateAccessToken("usuario@correo.com");
        assertThat(provider.validateToken(token)).isTrue();
        assertThat(provider.getUsernameFromToken(token)).isEqualTo("usuario@correo.com");
    }

    @Test
    void rechazaTokenExpirado() {
        ReflectionTestUtils.setField(provider, "accessTokenExpirationMs", -1000L);
        String token = provider.generateAccessToken("usuario@correo.com");
        assertThat(provider.validateToken(token)).isFalse();
        assertThat(provider.getUsernameFromToken(token)).isNull();
    }

    @Test
    void rechazaTokenManipulado() {
        String token = provider.generateAccessToken("usuario@correo.com");
        String manipulado = token.substring(0, token.length() - 4) + "xxxx";
        assertThat(provider.validateToken(manipulado)).isFalse();
    }

    @Test
    void rechazaTokenFirmadoConOtroSecreto() {
        JwtTokenProvider otro = new JwtTokenProvider();
        ReflectionTestUtils.setField(otro, "jwtSecret",
                "b3Ryby1zZWNyZXRvLWRpc3RpbnRvLXBhcmEtdGVzdC0zMi1ieXRlcw==");
        ReflectionTestUtils.setField(otro, "accessTokenExpirationMs", 900000L);
        ReflectionTestUtils.setField(otro, "refreshTokenExpirationMs", 604800000L);

        String ajeno = otro.generateAccessToken("usuario@correo.com");
        assertThat(provider.validateToken(ajeno)).isFalse();
    }
}
