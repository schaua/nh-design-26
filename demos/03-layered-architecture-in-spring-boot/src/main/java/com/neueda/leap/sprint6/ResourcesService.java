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

    public String describeResources(String member) {
        Object totalResources = repository.findTotalResources(member);
        Instant asOf = clock.instant();
        return "Member " + member + " has " + totalResources + " resources " + asOf + ".";
    }
}
