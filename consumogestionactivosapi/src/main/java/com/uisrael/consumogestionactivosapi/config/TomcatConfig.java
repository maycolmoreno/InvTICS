package com.uisrael.consumogestionactivosapi.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.tomcat.ConfigurableTomcatWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    @Bean
    public WebServerFactoryCustomizer<ConfigurableTomcatWebServerFactory> multipartFileCountCustomizer() {
        return factory -> factory.addConnectorCustomizers((Connector connector) ->
                connector.setMaxParameterCount(500)
        );
    }
}
