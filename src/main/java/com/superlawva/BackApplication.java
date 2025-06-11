package com.superlawva;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackApplication {
    public static void main(String[] args) {
        // ① .env 로드 → ② 각 entry 를 System property 로 주입
        io.github.cdimascio.dotenv.Dotenv
                .configure()
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load()
                .entries()
                .forEach(e -> System.setProperty(e.getKey(), e.getValue()));

        SpringApplication.run(BackApplication.class, args);
    }
}
