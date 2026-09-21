package com.neueda.leap.sprint6;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// THIS is the actual payoff of layering + constructor DI, from Module 3's
// objectives: testing PortfolioService needs no Spring context, no embedded
// Tomcat, no real database - just two mocks and a constructor call. Compare
// how fast this test runs to how long `mvn spring-boot:run` takes to start.
//
// This is Sprint 5 Module 11's isolation lesson again: PortfolioService is
// tested completely separately from whatever ResourcesRepository turns out
// to be (a Map today, Postgres from Module 7 onward) and from real wall-clock
// time (Clock is mocked too, so the test is deterministic).
@ExtendWith(MockitoExtension.class)
class ResourcesServiceTest {

    @Mock
    private ResourcesRepository repository;

    @Mock
    private Clock clock;

    @Test
    void describesTheClientsValueUsingTheInjectedClock() {
        when(repository.findTotalResources("M001")).thenReturn(42);
        when(clock.instant()).thenReturn(Instant.parse("2026-01-01T00:00:00Z"));

        ResourcesService service = new ResourcesService(repository, clock);

        String result = service.describeResources("M001");

        assertTrue(result.contains("M001"));
        assertTrue(result.contains("42"));
        assertTrue(result.contains("2026-01-01T00:00:00Z"));
        verify(repository).findTotalResources("M001");
    }
}
