package com.uisrael.gestionactivosapi.infraestructura.configuracion;

import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.credentials-path:}")
    private String credentialsPath;

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        if (credentialsPath == null || credentialsPath.isBlank()) {
            log.warn("Firebase no configurado: firebase.credentials-path esta vacio. Push notifications deshabilitadas.");
            return null;
        }
        try {
            FileInputStream serviceAccount = new FileInputStream(credentialsPath);
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
            log.info("Firebase inicializado correctamente. Push notifications habilitadas.");
            return FirebaseMessaging.getInstance();
        } catch (IOException e) {
            log.warn("No se pudo inicializar Firebase: {}. Push notifications deshabilitadas.", e.getMessage());
            return null;
        }
    }
}
