package com.neueda.leap.sprint6;

import java.sql.Date;



// Result mapping record for members loan history
public record Loan(int loanId, 
    String memberId,
    String memberName,
    String title,
    String resourceId,
    Date loanDate,
    Date returnDate) {}
