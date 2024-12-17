package com.rocketscreener.services;

import java.util.List;
import java.util.Map;

public interface DataSourceService {
    // e.g. fetch current prices, volumes for a list of symbols
    Map<String, Double> fetchCurrentMetrics(String metric, List<String> symbols);
}
