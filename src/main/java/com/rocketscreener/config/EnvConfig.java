package com.rocketscreener.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

@Service
public class ExampleService {

    private final Dotenv dotenv;

    public ExampleService(Dotenv dotenv) {
        this.dotenv = dotenv;
    }

    public String getSecretKey() {
        return dotenv.get("SECRET_KEY");
    }
}
