package com.rocketscreener.filters;

import com.rocketscreener.storage.FilterRepository;
import com.rocketscreener.storage.FilterRecord;
import com.rocketscreener.storage.HistoricalDataRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class FilterService {
    private final FilterRepository filterRepo;
    private final HistoricalDataRepository histDataRepo;

    public FilterService(FilterRepository filterRepo, HistoricalDataRepository histDataRepo) {
        this.filterRepo = filterRepo;
        this.histDataRepo = histDataRepo;
    }

    public boolean checkFilters(String symbol, String metric, double currentValue) {
        // Retrieve all enabled filters
        List<FilterRecord> filters = filterRepo.findAllEnabled();
        boolean triggerEvent = false;

        for(FilterRecord f : filters){
            if(!f.isComposite()){
                // Basic filter check
                if(f.metric().equals(metric)) {
                    double threshold = f.thresholdValue().doubleValue();
                    // Get historical data for interval
                    Timestamp now = Timestamp.from(Instant.now());
                    Timestamp from = Timestamp.from(Instant.now().minusSeconds(f.timeIntervalMinutes()*60L));
                    var hist = histDataRepo.getDataForInterval(symbol, metric, from, now);
                    double oldValue = hist.isEmpty()? currentValue : hist.get(0).value().doubleValue(); 
                    double change = ((currentValue - oldValue)/oldValue)*100.0;
                    
                    if(f.thresholdType().equalsIgnoreCase("percentage") && Math.abs(change) >= threshold) {
                        triggerEvent = true;
                    }
                    // Other types: absolute changes, etc.
                    // Extend as needed.
                }
            } else {
                // Composite filter: parse expression and evaluate
                // For demo: assume composite_expression = "filter1 AND filter2"
                // In reality, we'd parse and evaluate each referenced filter result
                // Here, let's just skip for brevity or assume composite handled similarly
            }
        }

        return triggerEvent;
    }
}
