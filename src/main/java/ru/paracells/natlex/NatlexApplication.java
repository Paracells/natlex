package ru.paracells.natlex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
public class NatlexApplication {

    public static void main(String[] args) {
        SpringApplication.run(NatlexApplication.class, args);
    }


}
