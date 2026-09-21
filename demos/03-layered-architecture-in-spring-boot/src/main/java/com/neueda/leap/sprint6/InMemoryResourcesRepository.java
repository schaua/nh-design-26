package com.neueda.leap.sprint6;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository 
public class InMemoryResourcesRepository implements ResourcesRepository {
    private Map<String, Integer> resources = new HashMap<String, Integer>();
    
    public InMemoryResourcesRepository() {
        // Initialize with some dummy data
        resources.put("M001", 4);
        resources.put("M002", 2);
    }
    
    @Override
    public Object findTotalResources(String member) {
        return resources.getOrDefault(member, 0);
    }
}
