package com.neueda.leap.sprint6;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoanController {

    private final ResourceRepository repository;

    public LoanController(ResourceRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/resources")
    public ResponseEntity<List<Resource>> getAllResources() {
        return ResponseEntity.ok(repository.findAll());
    }   

    @GetMapping("/resources/{resourceId}")
    public ResponseEntity<Resource> getResource(@PathVariable String resourceId) {
        repository.findById(resourceId);
        return repository.findByResourceId(resourceId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
