package com.neueda.leap.sprint6;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// The DTO (Data Transfer Object) - the shape of what crosses the HTTP
// boundary, straight from Module 5's OpenAPI LoanRequest schema. This is
// deliberately NOT the same class as any internal domain object - there is
// no "Loan" class here at all, only what a client is required to send.
//
// A record, because a DTO has no behaviour - it's pure data, and Bean
// Validation annotations work the same way on record components as on
// regular fields.
public record LoanRequestDto(

        @NotBlank(message = "ISBN is required")
        String ISBN,

        @NotNull(message = "resourceType is required")
        ResourceType resourceType,

        @NotBlank(message = "memberId must be positive")
        String memberId,

        @NotNull(message = "side is required")
        Side side
) {
}
