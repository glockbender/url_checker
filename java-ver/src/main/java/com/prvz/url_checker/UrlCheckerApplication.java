package com.prvz.url_checker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(
        scanBasePackages = "com.prvz.url_checker",
        exclude = {DataSourceAutoConfiguration.class})
public class UrlCheckerApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrlCheckerApplication.class);
    }

}
