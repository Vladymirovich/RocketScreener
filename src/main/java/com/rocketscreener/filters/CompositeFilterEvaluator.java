package com.rocketscreener.filters;

import com.rocketscreener.storage.FilterRepository;
import com.rocketscreener.storage.FilterRecord;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CompositeFilterEvaluator {
    private final FilterRepository filterRepo;
    private final FilterService filterService;

    public CompositeFilterEvaluator(FilterRepository filterRepo, FilterService filterService) {
        this.filterRepo = filterRepo;
        this.filterService = filterService;
    }

    /**
     * Evaluate a composite expression like "filterA AND filterB"
     * We assume simple logical expressions for this demo.
     */
    public boolean evaluate(String expression, String symbol, String metric, double currentValue) {
        // This is a simplified parser:
        // Split by 'AND' or 'OR'
        // For real cases, we might need a proper parser.
        if(expression.contains("AND")){
            String[] parts = expression.split("AND");
            boolean result = true;
            for(String p : parts){
                String fName = p.trim();
                result = result && checkSingleFilterByName(fName, symbol, metric, currentValue);
            }
            return result;
        } else if(expression.contains("OR")){
            String[] parts = expression.split("OR");
            boolean result = false;
            for(String p : parts){
                String fName = p.trim();
                result = result || checkSingleFilterByName(fName, symbol, metric, currentValue);
            }
            return result;
        } else {
            // Just a single filter name
            return checkSingleFilterByName(expression.trim(), symbol, metric, currentValue);
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
                // Use filterService to check single filter condition
                return filterService.checkSingleFilter(fr, symbol, metric, currentValue);
            }
        }
        return false;
    }
}
