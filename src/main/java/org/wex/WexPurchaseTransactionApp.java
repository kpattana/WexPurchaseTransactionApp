package org.wex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class WexPurchaseTransactionApp {
    public static void main(String[] arg) {
        SpringApplication.run(WexPurchaseTransactionApp.class, arg);
    }

    @Bean
    public RestTemplate getAppRestTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
