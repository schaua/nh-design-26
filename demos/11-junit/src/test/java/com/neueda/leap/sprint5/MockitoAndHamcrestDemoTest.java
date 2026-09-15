package com.neueda.leap.sprint5;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

// WHY ISOLATION MATTERS, AND WHY THAT MEANS MOCKS
//
// LoanManager's actual job is small: ask its LibraryResource for a fee, ask its
// ReportWriter to write a line, return the fee. None of that job involves
// KNOWING HOW a fee is calculated, or HOW a line gets written - it just
// coordinates two collaborators.
//
// Every test of LoanManager so far (Module 8's demo) used a REAL
// Book and a REAL InMemoryReportWriter. That means those tests were
// never truly testing LoanManager alone - they were testing LoanManager
// AND Book's fee formula AND InMemoryReportWriter's storage, all at
// once. If Book's fee calculation had a bug, EVERY test that used a
// real Book would fail too - including tests whose actual job is to
// check LoanManager's coordination logic, which might be completely correct.
// You'd see a red test and have no idea, without investigating, which of the
// two classes actually broke.
//
// A MOCK replaces a real collaborator with a fake stand-in whose behaviour YOU
// control completely, and that records every call made to it so you can check
// afterward. Testing LoanManager against a MOCK LibraryResource and a MOCK
// ReportWriter means: if this test goes red, it is unambiguously LoanManager's
// fault. Nothing else is even real enough to have a bug.
@ExtendWith(MockitoExtension.class)
@DisplayName("LoanManager, tested in true isolation with Mockito")
class MockitoAndHamcrestDemoTest {

    @Mock
    private LibraryResource mockResource;

    @Mock
    private ReportWriter mockWriter;

    @Nested
    @DisplayName("stubbing: controlling what a mock returns")
    class Stubbing {

        @Test
        @DisplayName("when(...).thenReturn(...) makes the mock respond however the test needs")
        void managerUsesWhateverFeeTheResourceReturns() {
            // Stub: "WHEN calculateFee is called with 5.0, THEN return 42.0" -
            // regardless of what a real LibraryResource subclass would actually compute.
            // This is the entire point: the test controls the collaborator's
            // behaviour directly, instead of depending on real business logic
            // living somewhere else.
            when(mockResource.calculateFee(5.0)).thenReturn(42.0);

            LoanRequest loanRequest = new LoanRequest("P001", mockResource, 5.0);
            LoanManager manager = new LoanManager(mockWriter);

            double fee = manager.processLoan(loanRequest);

            assertEquals(42.0, fee);
        }

        @Test
        @DisplayName("anyDouble() stubs a return value for ANY argument, when the exact value doesn't matter")
        void stubbingWithAnArgumentMatcher() {
            when(mockResource.calculateFee(anyDouble())).thenReturn(10.0);

            LoanRequest loanRequest = new LoanRequest("P002", mockResource, 7.5);
            double fee = new LoanManager(mockWriter).processLoan(loanRequest);

            assertEquals(10.0, fee);
        }
    }

    @Nested
    @DisplayName("verify(): checking a mock was actually used correctly")
    class Verifying {

        @Test
        @DisplayName("verify() confirms a specific call happened, with specific arguments")
        void verifiesTheWriterReceivedTheExpectedLine() {
            when(mockResource.calculateFee(3.0)).thenReturn(42.0);
            LoanRequest loanRequest = new LoanRequest("P001", mockResource, 3.0);

            new LoanManager(mockWriter).processLoan(loanRequest);

            // Not just "did processLoan() return the right number" - this proves
            // LoanManager actually TALKED to its ReportWriter collaborator,
            // with exactly the line it should have produced.
            verify(mockWriter).write("P001: $42.0");
        }

        @Test
        @DisplayName("times() confirms HOW MANY times a call happened")
        void verifiesTheResourceWasAskedExactlyOnce() {
            when(mockResource.calculateFee(anyDouble())).thenReturn(10.0);
            LoanRequest loanRequest = new LoanRequest("P002", mockResource, 2.0);

            new LoanManager(mockWriter).processLoan(loanRequest);

            verify(mockResource, times(1)).calculateFee(2.0);
            verify(mockWriter, times(1)).write(anyString());
        }

        @Test
        @DisplayName("verifyNoInteractions() confirms a mock was never touched at all")
        void aMockNeverCalledHasNoInteractions() {
            // Nothing in this test ever calls mockWriter - proving that, e.g., a
            // validation failure path genuinely never reaches the report writer,
            // rather than trusting that by reading the code.
            verifyNoInteractions(mockWriter);
        }
    }

    @Nested
    @DisplayName("ArgumentCaptor: inspecting exactly what was passed to a mock")
    class Capturing {

        @Test
        @DisplayName("captures the real argument for a closer assertion than an exact-match verify()")
        void capturesTheLineWrittenForDetailedInspection() {
            when(mockResource.calculateFee(anyDouble())).thenReturn(7.5);
            LoanRequest loanRequest = new LoanRequest("P004", mockResource, 3.0);

            new LoanManager(mockWriter).processLoan(loanRequest);

            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            verify(mockWriter).write(captor.capture());

            assertThat(captor.getValue(), allOf(containsString("P004"), containsString("7.5")));
        }
    }

    @Nested
    @DisplayName("mocks vs. Module 8's hand-rolled InMemoryReportWriter")
    class MocksVersusHandRolledFakes {

        @Test
        @DisplayName("a hand-rolled fake still uses REAL collaborator logic elsewhere")
        void theHandRolledFakeOnlyReplacesTheWriterNotTheResource() {
            // InMemoryReportWriter (Module 8) is a genuine, useful test double -
            // but notice this test still exercises a REAL Book. A bug
            // in Book.calculateFee() would make THIS test fail too,
            // even though it's meant to test LoanManager.
            InMemoryReportWriter fakeWriter = new InMemoryReportWriter();
            LibraryResource realBook = new Book("Dune");
            LoanRequest loanRequest = new LoanRequest("P003", realBook, 8.0);

            new LoanManager(fakeWriter).processLoan(loanRequest);

            assertEquals(List.of("P003: $5.0"), fakeWriter.getLines());
        }

        @Test
        @DisplayName("a mock isolates LoanManager from BOTH collaborators, not just one")
        void theMockIsolatesFromBothCollaborators() {
            when(mockResource.calculateFee(anyDouble())).thenReturn(999.0);
            LoanRequest loanRequest = new LoanRequest("P003", mockResource, 8.0);

            double fee = new LoanManager(mockWriter).processLoan(loanRequest);

            // 999.0 is not a real fee any Book would ever produce -
            // and that's exactly the point. This test could not possibly be
            // affected by a bug in Book, because Book was
            // never involved at all.
            assertEquals(999.0, fee);
        }
    }

    @Nested
    @DisplayName("Hamcrest matchers: readable, composable assertions")
    class HamcrestMatchers {

        @Test
        @DisplayName("assertThat(value, matcher) reads closer to a sentence than assertEquals")
        void basicHamcrestMatchers() {
            when(mockResource.calculateFee(anyDouble())).thenReturn(42.0);
            double fee = new LoanManager(mockWriter).processLoan(new LoanRequest("P005", mockResource, 2.0));

            // assertEquals(42.0, fee) and assertThat(fee, is(42.0)) check the
            // same thing - Hamcrest's phrasing reads more like the requirement
            // it's checking, and its matchers COMPOSE (see below).
            assertThat(fee, is(42.0));
            assertThat(fee, greaterThan(0.0));
        }

        @Test
        @DisplayName("string and collection matchers express intent precisely")
        void stringAndCollectionMatchers() {
            assertThat("Late Fee Report\nP001: $42.0", containsString("P001"));
            assertThat("Late Fee Report\nP001: $42.0", startsWith("Late"));

            List<String> lines = List.of("first", "second", "third");
            assertThat(lines, hasSize(3));
            assertThat(lines, hasItem("second"));
        }

        @Test
        @DisplayName("allOf/anyOf compose several matchers into one readable assertion")
        void composedMatchers() {
            double fee = 42.0;

            // A plain JUnit equivalent would need two separate assertTrue calls,
            // or a manual (fee > 0 && fee < 100) boolean expression with no
            // useful failure message. This single line checks both AND reports
            // exactly which part failed if it doesn't hold.
            assertThat(fee, allOf(greaterThan(0.0), lessThan(100.0)));
        }
    }
}
