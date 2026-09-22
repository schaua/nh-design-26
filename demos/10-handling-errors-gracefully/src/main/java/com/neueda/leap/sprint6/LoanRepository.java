package com.neueda.leap.sprint6;

public interface LoanRepository {
    boolean findAvailability(String resourceId);
    int loansByMember(String memberId);
}
