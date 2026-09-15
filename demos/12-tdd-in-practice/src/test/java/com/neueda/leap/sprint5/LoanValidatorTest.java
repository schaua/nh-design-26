package com.neueda.leap.sprint5;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// The final test suite from the live TDD session - each test was added ONE AT A
// TIME, in this order. See demo-guide.md for the cycle-by-cycle build.
// This suite validates loan requests for a library management system.
@DisplayName("LoanValidator")
class LoanValidatorTest {

    private final LoanValidator validator = new LoanValidator();

    @Test
    @DisplayName("accepts a straightforward borrow request with no fees and available slots")
    void acceptsAValidBorrowRequest() {
        LibraryResource book = new Book("ISBN001");
        LoanRequest request = new LoanRequest(book, 0, 0);

        OutstandingFees result = validator.validate(request);

        assertTrue(result.canBorrow());
    }

    @Nested
    @DisplayName("when the loan constraints are violated")
    class WhenLoanConstraintsViolated {

        @Test
        @DisplayName("rejects borrowing when the member has reached 5 loans")
        void rejectsWhenAtMaxLoans() {
            LibraryResource book = new Book("ISBN002");
            LoanRequest request = new LoanRequest(book, 0, 5);

            OutstandingFees result = validator.validate(request);

            assertFalse(result.canBorrow());
            assertEquals("member has reached the maximum of 5 loans", result.getReason());
        }

        @Test
        @DisplayName("accepts borrowing when member has exactly 4 loans")
        void acceptsWhenBelowMaxLoans() {
            LibraryResource book = new Book("ISBN003");
            LoanRequest request = new LoanRequest(book, 0, 4);

            OutstandingFees result = validator.validate(request);

            assertTrue(result.canBorrow());
        }
    }

    @Nested
    @DisplayName("when fees are a concern")
    class WhenFeesAreConcern {

        @Test
        @DisplayName("rejects borrowing when member has unpaid fees over £5")
        void rejectsWhenFeesTooHigh() {
            LibraryResource dvd = new DVD("DVD001");
            LoanRequest request = new LoanRequest(dvd, 5.50, 2);

            OutstandingFees result = validator.validate(request);

            assertFalse(result.canBorrow());
            assertTrue(result.getReason().contains("unpaid fees"));
            assertTrue(result.getReason().contains("exceeds"));
        }

        @Test
        @DisplayName("accepts borrowing when member has exactly £5.00 in fees")
        void acceptsWhenFeesExactlyAtLimit() {
            LibraryResource magazine = new Magazine("MAG001");
            LoanRequest request = new LoanRequest(magazine, 5.00, 1);

            OutstandingFees result = validator.validate(request);

            assertTrue(result.canBorrow());
        }

        @Test
        @DisplayName("accepts borrowing when member has £4.99 in fees")
        void acceptsWhenFeesJustUnderLimit() {
            LibraryResource book = new Book("ISBN004");
            LoanRequest request = new LoanRequest(book, 4.99, 1);

            OutstandingFees result = validator.validate(request);

            assertTrue(result.canBorrow());
        }
    }

    @Nested
    @DisplayName("when both loan count and fees are at their limits")
    class WhenMultipleConstraints {

        @Test
        @DisplayName("rejects when at max loans even if fees are acceptable")
        void rejectsMaxLoansOverrideFees() {
            LibraryResource book = new Book("ISBN005");
            LoanRequest request = new LoanRequest(book, 3.00, 5);

            OutstandingFees result = validator.validate(request);

            assertFalse(result.canBorrow());
            assertEquals("member has reached the maximum of 5 loans", result.getReason());
        }

        @Test
        @DisplayName("rejects when fees are too high even if below max loans")
        void rejectsHighFeesOverrideLoanCount() {
            LibraryResource dvd = new DVD("DVD002");
            LoanRequest request = new LoanRequest(dvd, 10.00, 2);

            OutstandingFees result = validator.validate(request);

            assertFalse(result.canBorrow());
            assertTrue(result.getReason().contains("unpaid fees"));
        }
    }
}
