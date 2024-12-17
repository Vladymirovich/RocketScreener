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
     * Проверяет, соответствует ли текущий показатель фильтру.
     *
     * @param filter        Фильтр для проверки.
     * @param symbol        Символ (например, тикер криптовалюты).
     * @param metric        Метрика, которую необходимо проверить.
     * @param currentValue  Текущее значение метрики.
     * @return true, если текущий показатель соответствует фильтру, иначе false.
     */
    public boolean checkSingleFilter(FilterRecord filter, String symbol, String metric, double currentValue) {
        if (!filter.metric().equalsIgnoreCase(metric)) {
            // Метрика не совпадает
            return false;
        }

        BigDecimal threshold = filter.thresholdValue();
        String thresholdType = filter.thresholdType().toLowerCase();

        switch (thresholdType) {
            case "greater_than":
                return BigDecimal.valueOf(currentValue).compareTo(threshold) > 0;
            case "greater_than_or_equal":
                return BigDecimal.valueOf(currentValue).compareTo(threshold) >= 0;
            case "less_than":
                return BigDecimal.valueOf(currentValue).compareTo(threshold) < 0;
            case "less_than_or_equal":
                return BigDecimal.valueOf(currentValue).compareTo(threshold) <= 0;
            case "equal":
                return BigDecimal.valueOf(currentValue).compareTo(threshold) == 0;
            case "not_equal":
                return BigDecimal.valueOf(currentValue).compareTo(threshold) != 0;
            default:
                // Неизвестный тип порога
                return false;
        }
    }

    /**
     * Дополнительные методы сервиса по необходимости.
     */

    /**
     * Метод для примера: получение всех фильтров.
     *
     * @return Список всех включённых фильтров.
     */
    public List<FilterRecord> getAllEnabledFilters() {
        return filterRepository.findAllEnabled();
    }

    /**
     * Метод для обновления фильтра.
     *
     * @param filterId Идентификатор фильтра.
     * @param enabled  Статус включения фильтра.
     */
    public void updateFilterStatus(int filterId, boolean enabled) {
        filterRepository.setFilterEnabled(filterId, enabled);
    }

    /**
     * Метод для удаления фильтра.
     *
     * @param filterId Идентификатор фильтра.
     */
    public void deleteFilter(int filterId) {
        // Реализуйте логику удаления фильтра, если необходимо.
        // Например, можно добавить метод в FilterRepository для удаления.
    }
}
