package com.rocketscreener.filters;

import com.rocketscreener.storage.FilterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class FilterServiceTest {

    @Mock
    private FilterRepository filterRepo;

    @InjectMocks
    private FilterService filterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFilterLogic() {
        // Добавьте реализацию теста с использованием mock filterRepo
        verifyNoMoreInteractions(filterRepo);
    }
}
