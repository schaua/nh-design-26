package com.neueda.leap.sprint6;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.NoSuchElementException;


import jakarta.validation.Valid;
@RestController 
public class LoanController {   

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping 
    public ResponseEntity<Loan> addLoan(@Valid @RequestBody Loan loan) {
        Loan createdLoan = loanService.addLoan(loan);
        return ResponseEntity.ok(createdLoan);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Loan> getLoan(@PathVariable("id") int loanId) {
        Loan loan = loanService.getLoan(loanId);
        if (loan != null) {
            return ResponseEntity.ok(loan);
        } else {
            throw new NoSuchElementException("Loan not found");
        }
    }
}
