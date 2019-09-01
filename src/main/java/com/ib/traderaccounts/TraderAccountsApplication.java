package com.ib.traderaccounts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

/**
 * The main method. Where it all starts. Spring Boot app so not much to see here
 */
@SpringBootApplication
@EnableJms
public class TraderAccountsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TraderAccountsApplication.class, args);
    }
}
