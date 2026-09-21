package com.neueda.leap.sprint6;

import java.time.Clock;
import java.time.Instant;

import org.springframework.stereotype.Service;

/**
 * ResourcesService
 */
@Service
public class ResourcesService {
    public ResourcesService(ResourcesRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }
    
    private final ResourcesRepository repository;
    private final Clock clock;

    public String describeResources(String string) {
        Object totalResources = repository.findTotalResources(string);
        Instant asOf = clock.instant();
        return "Member " + string + " has " + totalResources + " resources " + asOf + ".";
    }
}
