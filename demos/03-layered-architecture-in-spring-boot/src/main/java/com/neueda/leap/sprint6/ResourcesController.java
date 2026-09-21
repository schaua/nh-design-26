package com.neueda.leap.sprint6;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController 
public class ResourcesController {
    private final ResourcesService service;

    public ResourcesController(ResourcesService service) {
        this.service = service;
    }   
    @GetMapping("/resources/{member}")
    public String describeResources(@PathVariable String member) {
        return service.describeResources(member);
    }   

    @GetMapping("/resources")
    public String describeResources() {
        return "<all resources>";
    }   
}
