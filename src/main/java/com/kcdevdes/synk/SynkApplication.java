package com.kcdevdes.synk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SynkApplication {

    public static void main(String[] args) {
        SpringApplication.run(SynkApplication.class, args);
    }

}
