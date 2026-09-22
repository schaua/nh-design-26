package com.neueda.leap.sprint6;

import java.time.LocalDate;

// Also a DTO, matching Module 5's OpenAPI OrderResponse schema exactly. Note
// what's NOT here: no reference to Instrument, no internal fee-calculation
// detail beyond the final number. A client only ever sees this shape -
// changing an internal domain class never has to mean changing this contract,
// and vice versa.
public record LoanResponseDto(String resourceId, String status,LocalDate dueDate, String reason) {
}
