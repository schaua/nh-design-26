package com.neueda.leap.sprint6;

import java.util.List;

import org.springframework.stereotype.Service;

/**
 * ResourceService
 */
@Service 
public class ResourceService {
    
    private final ResourceMapper resourceMapper;    

    public ResourceService(ResourceMapper resourceMapper) {
        this.resourceMapper = resourceMapper;
    }
    public List<Resource> findAll() {
        return resourceMapper.findAll();
    }

    public Resource findByResourceId(String resourceId) {
        return resourceMapper.findByResourceId(resourceId);
    }

}
