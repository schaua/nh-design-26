package com.neueda.leap.sprint6;

import org.springframework.stereotype.Service;

@Service
public class LoanService {

    private final ResourcesRepository repository;

    public LoanService(ResourcesRepository repository) {
        this.repository = repository;
    }

    public boolean isAvailable(String iSBN) {
        Resource resource = repository.findByISBN(iSBN);
        return resource != null && resource.isAvailable();
    }
}
