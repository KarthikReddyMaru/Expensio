package com.cashigo.expensio;

import com.cashigo.expensio.common.documentation.AppDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@AppDoc
public class ExpensioApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpensioApplication.class, args);
    }

}
