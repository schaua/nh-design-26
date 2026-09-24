package com.neueda.leap.sprint6;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

@Service 
public class LoanService {
    
    private final LoanMapper loanMapper;

    public LoanService(LoanMapper loanMapper) {
        this.loanMapper = loanMapper;
    }   

    public List<Loan> getLoans(String memberId) {
        return loanMapper.findByMemberId(memberId);
    }

    public Loan addLoan(Loan loan) {
        loanMapper.addLoan(loan);
        return loan;
    }

    public Loan getLoan(int loanId) {   
        Loan loan = loanMapper.findById(loanId);
        if (loan == null) {
            throw new NoSuchElementException("Loan not found");
        }
        return loan;
    }
    
}
