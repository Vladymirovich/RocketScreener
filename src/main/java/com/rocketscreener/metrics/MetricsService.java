package com.rocketscreener.metrics;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import java.net.http.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@Service
public class MetricsService {
    private final String grafanaUrl;
    private final String prometheusUrl;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public MetricsService(Dotenv dotenv) {
        this.grafanaUrl = dotenv.get("GRAFANA_URL");
        this.prometheusUrl = dotenv.get("PROMETHEUS_URL");
    }

    public String getPrometheusMetrics() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(prometheusUrl))
                    .GET().build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return resp.body();
        }catch(Exception e){
            e.printStackTrace();
            return "Error fetching metrics";
        }
    }

    // Similar methods to interact with Grafana API if needed
}
