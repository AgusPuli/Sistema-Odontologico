package com.bs.odontograma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OdontogramaApplication {

    public static void main(String[] args) {
        SpringApplication.run(OdontogramaApplication.class, args);
    }

}
