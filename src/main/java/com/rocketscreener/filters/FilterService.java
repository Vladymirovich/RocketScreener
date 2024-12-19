package com.rocketscreener.filters;

import com.rocketscreener.storage.FilterRecord;
import com.rocketscreener.storage.FilterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FilterService {

    private final FilterRepository filterRepository;

    @Autowired
    public FilterService(FilterRepository filterRepository) {
        this.filterRepository = filterRepository;
    }

    /**
     * Checks if the current value satisfies the filter criteria.
     *
     * @param filter        The filter to check against.
     * @param symbol        The symbol being evaluated.
     * @param metric        The metric being evaluated.
     * @param currentValue  The current value of the metric.
     * @return true if the current value satisfies the filter, else false.
     */
    public boolean checkSingleFilter(FilterRecord filter, String symbol, String metric, double currentValue) {
        if (!filter.metric().equalsIgnoreCase(metric)) {
            // Metric does not match
            return false;
        }

        BigDecimal threshold = filter.thresholdValue();
        String thresholdType = filter.thresholdType().toLowerCase();

        BigDecimal current = BigDecimal.valueOf(currentValue);

        switch (thresholdType) {
            case "greater_than":
                return current.compareTo(threshold) > 0;
            case "greater_than_or_equal":
                return current.compareTo(threshold) >= 0;
            case "less_than":
                return current.compareTo(threshold) < 0;
            case "less_than_or_equal":
                return current.compareTo(threshold) <= 0;
            case "equal":
                return current.compareTo(threshold) == 0;
            case "not_equal":
                return current.compareTo(threshold) != 0;
            default:
                // Unknown threshold type
                return false;
        }
    }

    /**
     * Retrieves all enabled filters.
     *
     * @return List of enabled FilterRecord objects.
     */
    public List<FilterRecord> getAllEnabledFilters() {
        return filterRepository.findAllEnabled();
    }

    /**
     * Updates the enabled status of a filter.
     *
     * @param filterId ID of the filter.
     * @param enabled  New enabled status.
     */
    public void updateFilterStatus(int filterId, boolean enabled) {
        filterRepository.setFilterEnabled(filterId, enabled);
    }

    /**
     * Deletes a filter.
     *
     * @param filterId ID of the filter to delete.
     */
    public void deleteFilter(int filterId) {
        filterRepository.deleteFilter(filterId);
    }

    // Additional service methods as needed
}
