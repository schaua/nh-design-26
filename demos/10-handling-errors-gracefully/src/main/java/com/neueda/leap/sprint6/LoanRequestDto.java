package com.neueda.leap.sprint6;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoanRequestDto(

        @NotBlank(message = "resourceId is required")
        String resourceId,

        @NotNull(message = "resourceType is required")
        ResourceType resourceType,

        @NotBlank(message = "memberId is required")
        String memberId,

        @NotNull(message = "side is required")
        Side side
) {
}
