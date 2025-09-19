package com.cashigo.expensio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ExpensioApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpensioApplication.class, args);
    }

}
