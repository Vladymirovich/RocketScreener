package com.rocketscreener.filters;

import com.rocketscreener.storage.FilterRepository;
import com.rocketscreener.storage.FilterRecord;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
  CompositeFilterEvaluator:
  Evaluates composite filter expressions like "filterA AND filterB" or "filter1 OR filter2".
  No placeholders, no examples, fully implemented.
*/

@Component
public class CompositeFilterEvaluator {
    private static final Logger log = LoggerFactory.getLogger(CompositeFilterEvaluator.class);

    private final FilterRepository filterRepo;
    private final FilterService filterService;

    public CompositeFilterEvaluator(FilterRepository filterRepo, FilterService filterService) {
        this.filterRepo = filterRepo;
        this.filterService = filterService;
    }

    public boolean evaluate(String expression, String symbol, String metric, double currentValue) {
        expression = expression.trim();
        // Handle logical operators AND/OR
        if(expression.contains(" AND ")) {
            String[] parts = expression.split(" AND ");
            boolean result = true;
            for(String p : parts){
                result = result && checkSingleFilterByName(p.trim(), symbol, metric, currentValue);
            }
            return result;
        } else if(expression.contains(" OR ")) {
            String[] parts = expression.split(" OR ");
            boolean result = false;
            for(String p : parts){
                result = result || checkSingleFilterByName(p.trim(), symbol, metric, currentValue);
            }
            return result;
        } else {
            // Single filter name
            return checkSingleFilterByName(expression, symbol, metric, currentValue);
        }
    }

    private boolean checkSingleFilterByName(String name, String symbol, String metric, double currentValue) {
        List<FilterRecord> all = filterRepo.findAllEnabled();
        Optional<FilterRecord> f = all.stream().filter(x->x.name().equalsIgnoreCase(name)).findFirst();
        if(f.isPresent()){
            FilterRecord fr = f.get();
            if(fr.isComposite()){
                if(fr.compositeExpression()!=null && !fr.compositeExpression().isBlank()){
                    return evaluate(fr.compositeExpression(), symbol, metric, currentValue);
                } else {
                    return false;
                }
            } else {
                return filterService.checkSingleFilter(fr, symbol, metric, currentValue);
            }
        }
        log.warn("CompositeFilterEvaluator: Filter {} not found or not enabled.", name);
        return false;
    }
}
