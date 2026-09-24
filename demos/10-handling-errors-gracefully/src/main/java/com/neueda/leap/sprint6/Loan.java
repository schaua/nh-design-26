package com.neueda.leap.sprint6;

import java.sql.Date;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;



// Result mapping record for members loan history
public record Loan(int loanId, 
    @NotEmpty String memberId,
    @NotEmpty String memberName,
    String title,
    @NotEmpty String resourceId,
    @NotNull Date loanDate,
    Date returnDate) {}
