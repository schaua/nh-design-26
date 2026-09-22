package com.neueda.leap.sprint6;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.NoSuchElementException;

@Repository
public class InMemoryLoanRepository implements LoanRepository {

    private static final Map<String, Boolean> SHELVES = Map.of(
            "BOOK1", true,
            "BOOK2", false,
            "BOOK3", true,
            "BOOK4", false
    );
    private static final Map<String, Integer> MEMBER_LOANS = Map.of(
            "MEMBER1", 2,
            "MEMBER2", 0,
            "MEMBER3", 1
    );

    @Override
    public boolean findAvailability(String resourceId) {
        Boolean availability = SHELVES.getOrDefault(resourceId, null);
        if (availability == null) {
            throw new NoSuchElementException("no such resource: " + resourceId);
        }
        return availability;
    }

    @Override
    public int loansByMember(String memberId) {
        return MEMBER_LOANS.getOrDefault(memberId, 0);
    }
}
