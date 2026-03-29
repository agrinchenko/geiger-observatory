package com.fcmbp.geigerobservatory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GeigerObservatoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(GeigerObservatoryApplication.class, args);
    }
}
