package com.neueda.leap.sprint6;

import org.springframework.stereotype.Service;

@Service
public class LoanService {

    private static final double MAX_LOAN_VALUE = 5;

    private final LoanRepository repository;

    public LoanService(LoanRepository repository) {
        this.repository = repository;
    }

    public boolean isWithinMaxLoans(String resourceId, String memberId) {
        int currentLoans = repository.loansByMember(memberId);
        if (currentLoans >= MAX_LOAN_VALUE) {
            throw new LoanRejectedException(
                    "member " + memberId + " has reached the maximum number of loans: " + MAX_LOAN_VALUE);
        }
        return repository.findAvailability(resourceId);
    }
}
