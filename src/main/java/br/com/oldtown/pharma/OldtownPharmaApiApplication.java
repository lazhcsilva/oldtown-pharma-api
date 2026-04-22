package br.com.oldtown.pharma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class OldtownPharmaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OldtownPharmaApiApplication.class, args);
    }

}
