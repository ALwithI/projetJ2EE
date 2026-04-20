package com.taquin.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {
        "com.taquin.service",
        "com.taquin.repository"
})
@Import(DatabaseConfig.class)
public class AppConfig {
    // Contexte principal : services + base de données
}