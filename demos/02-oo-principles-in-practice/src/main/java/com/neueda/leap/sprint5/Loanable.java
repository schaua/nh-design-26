package com.neueda.leap.sprint5;

import java.time.LocalDate;

public interface Loanable {
    void loanTo(String memberId);
    void returnItem();

    boolean isLoaned();
    String getBorrowerId();
    LocalDate getDueDate();
}
