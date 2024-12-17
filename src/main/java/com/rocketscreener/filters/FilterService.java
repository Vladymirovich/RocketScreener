package com.rocketscreener.filters;

import com.rocketscreener.storage.FilterRepository;
import com.rocketscreener.storage.FilterRecord;
import com.rocketscreener.storage.HistoricalDataRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

/*
  FilterService evaluates filters (percentage, absolute) and composites.
  No placeholders, real logic.
*/

@Service
public class FilterService {
    private final FilterRepository filterRepo;
    private final HistoricalDataRepository histDataRepo;

    public FilterService(FilterRepository filterRepo, HistoricalDataRepository histDataRepo) {
        this.filterRepo = filterRepo;
        this.histDataRepo = histDataRepo;
    }

    public boolean checkFilters(String symbol, String metric, double currentValue) {
        List<FilterRecord> filters = filterRepo.findAllEnabled();
        boolean triggerEvent = false;

        for(FilterRecord f : filters){
            if(!f.isComposite()){
                if(checkSingleFilter(f, symbol, metric, currentValue)) {
                    triggerEvent = true;
                }
            } else {
                if(f.compositeExpression()!=null && !f.compositeExpression().isBlank()){
                    CompositeFilterEvaluator evaluator = com.rocketscreener.utils.SpringContext.getBean(CompositeFilterEvaluator.class);
                    if(evaluator.evaluate(f.compositeExpression(), symbol, metric, currentValue)){
                        triggerEvent = true;
                    }
                }
            }
        }

        return triggerEvent;
    }

    public boolean checkSingleFilter(FilterRecord f, String symbol, String metric, double currentValue) {
        if(!f.metric().equals(metric)) return false;

        double threshold = f.thresholdValue().doubleValue();
        Timestamp now = Timestamp.from(Instant.now());
        Timestamp from = Timestamp.from(Instant.now().minusSeconds(f.timeIntervalMinutes()*60L));
        var hist = histDataRepo.getDataForInterval(symbol, metric, from, now);
        double oldValue = hist.isEmpty()? currentValue : hist.get(0).value().doubleValue();
        double change = currentValue - oldValue;
        double percentChange = (oldValue==0)? 0 : (change / oldValue)*100.0;

        if(f.thresholdType().equalsIgnoreCase("percentage")) {
            return Math.abs(percentChange) >= threshold;
        } else if(f.thresholdType().equalsIgnoreCase("absolute")) {
            return Math.abs(change) >= threshold;
        }
        // Add more threshold types if needed
        return false;
    }
}
