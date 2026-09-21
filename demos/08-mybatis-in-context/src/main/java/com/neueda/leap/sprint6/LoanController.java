package com.neueda.leap.sprint6;

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

    @GetMapping("/resources/{resourceId}")
    public ResponseEntity<Resource> getResource(@PathVariable String resourceId) {
        return repository.findByResourceId(resourceId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
