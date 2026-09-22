package com.neueda.leap.sprint6;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// No Spring context - jakarta.validation's own Validator runs the annotations
// directly. This is exactly the same idea as Sprint 5's isolated unit tests:
// checking OrderRequestDto's validation rules doesn't require starting a web
// server.
class LoanRequestDtoTests {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeFactory() {
        factory.close();
    }

    @Test
    void aFullyValidRequestHasNoViolations() {
        LoanRequestDto request = new LoanRequestDto("Book1", ResourceType.BOOK, "M001", Side.CHECKOUT);

        Set<ConstraintViolation<LoanRequestDto>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void aBlankResourceIdIsRejected() {
        LoanRequestDto request = new LoanRequestDto("", ResourceType.BOOK, "M001", Side.CHECKOUT);

        Set<ConstraintViolation<LoanRequestDto>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("ISBN", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void aNullResourceTypeIsRejected() {
        LoanRequestDto request = new LoanRequestDto("Book1", null, "M001", Side.CHECKOUT);

        Set<ConstraintViolation<LoanRequestDto>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("resourceType", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void anEmptyMemberIdIsRejected() {
        LoanRequestDto request = new LoanRequestDto("Book1", ResourceType.BOOK, "", Side.CHECKOUT);

        Set<ConstraintViolation<LoanRequestDto>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("memberId", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void aNullSideIsRejected() {
        LoanRequestDto request = new LoanRequestDto("Book1", ResourceType.BOOK, "M001", null);

        Set<ConstraintViolation<LoanRequestDto>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("side", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void multipleViolationsAreAllReported() {
        LoanRequestDto request = new LoanRequestDto("", null, "", null);

        Set<ConstraintViolation<LoanRequestDto>> violations = validator.validate(request);

        assertEquals(4, violations.size());
    }
}
