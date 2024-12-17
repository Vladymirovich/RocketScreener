package com.rocketscreener.services;

import com.rocketscreener.storage.SourceRecord;
import com.rocketscreener.storage.SourceRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import io.github.cdimascio.dotenv.Dotenv;

/*
  DataAggregatorService:
  - Fetches enabled sources from DB
  - Sort by priority
  - Picks top priority source
  - Calls corresponding service (CoinMarketCap, Binance, ByBit, Coinbase, OKX, Kraken, Bitfinex, Gateio)
  - Returns requested metric
  No placeholders, all real logic.
*/

@Service
public class DataAggregatorService {
    private static final Logger log = LoggerFactory.getLogger(DataAggregatorService.class);

    private final SourceRepository sourceRepo;
    private final Dotenv dotenv;

    private final CoinMarketCapService cmcService;
    private final BinanceService binanceService;
    private final ByBitService bybitService;
    private final CoinbaseService coinbaseService;
    private final OKXService okxService;
    private final KrakenService krakenService;
    private final BitfinexService bitfinexService;
    private final GateioService gateioService;

    public DataAggregatorService(SourceRepository sourceRepo,
                                 Dotenv dotenv,
                                 CoinMarketCapService cmcService,
                                 BinanceService binanceService,
                                 ByBitService bybitService,
                                 CoinbaseService coinbaseService,
                                 OKXService okxService,
                                 KrakenService krakenService,
                                 BitfinexService bitfinexService,
                                 GateioService gateioService) {
        this.sourceRepo = sourceRepo;
        this.dotenv = dotenv;
        this.cmcService = cmcService;
        this.binanceService = binanceService;
        this.bybitService = bybitService;
        this.coinbaseService = coinbaseService;
        this.okxService = okxService;
        this.krakenService = krakenService;
        this.bitfinexService = bitfinexService;
        this.gateioService = gateioService;
    }

    public double getMetricForSymbol(String metric, String symbol) {
        List<SourceRecord> sources = sourceRepo.findAllEnabledSources();
        // Sort by priority ascending (lower priority number = higher priority)
        sources.sort(Comparator.comparingInt(SourceRecord::priority));

        for(SourceRecord src: sources) {
            double val = fetchFromSource(src, metric, symbol);
            if(!Double.isNaN(val)){
                return val;
            }
        }

        log.error("DataAggregatorService: No source returned a valid value for {} {}", metric, symbol);
        return Double.NaN;
    }

    private double fetchFromSource(SourceRecord src, String metric, String symbol){
        // Depending on src.type, call corresponding service
        // type could be: "cmc", "binance", "bybit", "coinbase", "okx", "kraken", "bitfinex", "gateio"
        // If unknown type, log error.
        String type = src.type().toLowerCase();
        List<String> symbols = Collections.singletonList(symbol);

        try {
            switch(type){
                case "coinmarketcap":
                case "cmc":
                    return cmcService.fetchCurrentMetrics(metric, symbols).getOrDefault(symbol, Double.NaN);
                case "binance":
                    return binanceService.fetchCurrentMetrics(metric, symbols).getOrDefault(symbol, Double.NaN);
                case "bybit":
                    return bybitService.fetchCurrentMetrics(metric, symbols).getOrDefault(symbol, Double.NaN);
                case "coinbase":
                    return coinbaseService.fetchCurrentMetrics(metric, symbols).getOrDefault(symbol, Double.NaN);
                case "okx":
                    return okxService.fetchCurrentMetrics(metric, symbols).getOrDefault(symbol, Double.NaN);
                case "kraken":
                    return krakenService.fetchCurrentMetrics(metric, symbols).getOrDefault(symbol, Double.NaN);
                case "bitfinex":
                    return bitfinexService.fetchCurrentMetrics(metric, symbols).getOrDefault(symbol, Double.NaN);
                case "gate.io":
                case "gateio":
                    return gateioService.fetchCurrentMetrics(metric, symbols).getOrDefault(symbol, Double.NaN);
                default:
                    log.error("Unknown source type: {}", src.type());
                    return Double.NaN;
            }
        } catch(Exception e){
            log.error("Error fetching metric {} for {} from {}", metric, symbol, src.name(), e);
            return Double.NaN;
        }
    }
}
