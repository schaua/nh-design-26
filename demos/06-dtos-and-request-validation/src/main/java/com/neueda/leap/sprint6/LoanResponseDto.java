package com.neueda.leap.sprint6;

import java.time.LocalDate;

// Also a DTO, matching Module 5's OpenAPI LoanResponse schema exactly. Note
// what's NOT here: no reference to Resource. A client only ever sees this shape -
// changing an internal domain class never has to mean changing this contract,
// and vice versa.
public record LoanResponseDto(String ISBN, String resourceType, String memberId, LocalDate dueDate, String reason) {
}
