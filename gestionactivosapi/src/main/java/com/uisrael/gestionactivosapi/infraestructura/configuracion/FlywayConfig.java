package com.uisrael.gestionactivosapi.infraestructura.configuracion;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.jpa.autoconfigure.EntityManagerFactoryDependsOnPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    @Bean
    public Flyway flyway(
            DataSource dataSource,
            @Value("${spring.flyway.locations:classpath:db/migration}") String locations,
            @Value("${spring.flyway.baseline-version:17}") String baselineVersion) {

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(locations)
                .baselineOnMigrate(true)
                .baselineVersion(baselineVersion)
                .load();

        flyway.repair();
        flyway.migrate();
        return flyway;
    }

    @Bean
    public static BeanFactoryPostProcessor flywayJpaDependency() {
        return new EntityManagerFactoryDependsOnPostProcessor("flyway");
    }
}
