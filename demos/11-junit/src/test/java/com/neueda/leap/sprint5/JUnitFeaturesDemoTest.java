package com.neueda.leap.sprint5;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

// Every test you've written this sprint used @Test and assertEquals/assertTrue.
// That's a small fraction of what JUnit offers. This demo walks through the rest,
// applied to classes you already know: Loan, LibraryResource, and its subclasses.
@DisplayName("JUnit 5 features, applied to library management")
class JUnitFeaturesDemoTest {

    // --- @BeforeEach: shared setup, re-run fresh before EVERY test ---
    // A new Loan is created before each test method runs, so no test can
    // accidentally see state left over from another one.
    private Loan loan;

    @BeforeEach
    void setUp() {
        loan = new Loan(100);
    }

    @Test
    @DisplayName("a fresh loan starts with the quantity it was constructed with")
    void freshLoanHasInitialQuantity() {
        assertEquals(100.0, loan.getQuantity(), 0.0001);
    }

    @Test
    @DisplayName("adjusting is reflected immediately")
    void adjustChangesQuantity() {
        loan.adjust(50);
        assertEquals(150.0, loan.getQuantity(), 0.0001);
    }

    // --- assertThrows: the FORMAL way to test for an expected exception ---
    // Compare to a manual try/catch (which some earlier labs used) - assertThrows
    // is shorter, and fails with a clear message if NO exception is thrown at all,
    // which a try/catch can silently miss if written carelessly.
    @Test
    @DisplayName("adjusting below zero throws, and does not change state")
    void adjustBelowZeroThrows() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> loan.adjust(-1000)
        );
        assertTrue(exception.getMessage().contains("negative"));
        // and the state is provably unchanged - this is a SECOND assertion,
        // grouped with assertAll below in a moment.
    }

    // --- assertAll: group related assertions so ALL of them run and report,
    // even if an earlier one fails. A chain of separate assertEquals calls
    // stops at the first failure - assertAll doesn't. ---
    @Test
    @DisplayName("a book's title and fee are both correct, reported together")
    void bookReportsTitleAndFeeTogether() {
        Book book = new Book("Dune");

        assertAll("book properties",
                () -> assertEquals("Dune", book.getTitle()),
                () -> assertEquals(5.0, book.calculateFee(10), 0.0001),
                () -> assertEquals(5.0, book.calculateFee(100), 0.0001)
        );
        // If the title assertion failed AND the fee assertion failed, assertAll
        // reports BOTH failures in one run - a chain of assertEquals would only
        // ever show you the first one, hiding the second until you fix the first.
    }

    // --- @ParameterizedTest + @ValueSource: run the SAME test body against
    // several different inputs, instead of copy-pasting near-identical tests. ---
    @ParameterizedTest
    @ValueSource(doubles = {1, 5, 30, 100})
    @DisplayName("a book's late fee is always exactly $5, regardless of days overdue")
    void bookFeeIsAlwaysFive(double daysOverdue) {
        assertEquals(5.0, new Book("To Kill a Mockingbird").calculateFee(daysOverdue), 0.0001);
    }

    // --- @ParameterizedTest + @CsvSource: when the test needs an input AND its
    // expected output, paired together. ---
    @ParameterizedTest
    @CsvSource({
            "1, 0.001",
            "10, 0.01",
            "100, 0.1"
    })
    @DisplayName("a magazine's late fee is 0.1% per day overdue")
    void magazineFeeIsPercentageOfDaysOverdue(double daysOverdue, double expectedFee) {
        assertEquals(expectedFee, new Magazine("Time").calculateFee(daysOverdue), 0.0001);
    }

    // --- @Nested: group related tests into their own inner class, so the test
    // report itself documents the structure of the behaviour being tested. ---
    @Nested
    @DisplayName("when the loan is at exactly zero")
    class WhenLoanIsAtZero {

        private Loan zeroLoan;

        @BeforeEach
        void setUp() {
            // Each @Nested class gets its OWN @BeforeEach - this runs in
            // addition to the outer class's setUp() above, not instead of it.
            zeroLoan = new Loan(0);
        }

        @Test
        @DisplayName("a positive adjustment succeeds")
        void positiveAdjustmentSucceeds() {
            zeroLoan.adjust(10);
            assertEquals(10.0, zeroLoan.getQuantity(), 0.0001);
        }

        @Test
        @DisplayName("any negative adjustment throws")
        void anyNegativeAdjustmentThrows() {
            assertThrows(IllegalArgumentException.class, () -> zeroLoan.adjust(-0.01));
        }
    }

    // --- @Disabled: skip a test deliberately, with a reason on record - very
    // different from just deleting or commenting it out, which loses the intent. ---
    @Test
    @Disabled("Audiobook resources aren't introduced until Module 7's OCP kata")
    void placeholderForFutureResourceType() {
        fail("not yet implemented");
    }
}
