package com.uisrael.consumogestionactivosapi.config;

import org.springframework.context.annotation.Configuration;

/**
 * La configuracion de Tomcat (max-part-count, max-parameter-count)
 * se gestiona via application.properties:
 *   server.tomcat.max-part-count=200
 *   spring.servlet.multipart.max-file-size=5MB
 *   spring.servlet.multipart.max-request-size=30MB
 */
@Configuration
public class TomcatConfig {
}
