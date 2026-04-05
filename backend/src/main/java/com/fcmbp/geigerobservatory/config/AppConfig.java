package com.fcmbp.geigerobservatory.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({IngestionProperties.class, DeviceProperties.class, AnalysisProperties.class})
public class AppConfig {
}
