package com.demo.upi_offline_mesh;

import com.demo.upi_offline_mesh.service.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UpiOfflineMeshApplication {

    public static void main(String[] args) {
        SpringApplication.run(UpiOfflineMeshApplication.class, args);
    }

    @Bean
    CommandLineRunner seedAccounts(AccountService accountService) {
        return args -> {
            accountService.createAccount("Alice", 10000);
            accountService.createAccount("Bob", 5000);
        };
    }
}