package com.rocketscreener;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
  Project repository: https://github.com/Vladymirovich/RocketScreener
  Deployment & run instructions:
    1) Clone repository
    2) docker-compose up -d (in docker/ folder)
    3) mvn clean install
    4) mvn spring-boot:run
*/

/* ChatGPT commentary: This improves reliability and enhances user experience. */

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));

        SpringApplication.run(Main.class, args);
    }
}
