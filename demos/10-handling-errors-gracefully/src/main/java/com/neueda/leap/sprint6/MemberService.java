package com.neueda.leap.sprint6;

import java.util.List;

import org.springframework.stereotype.Service;

@Service 
public class MemberService {
    
    // Lookup max number of loans per member from environment variable
    private static final int MAX_LOANS_PER_MEMBER = 
    Integer.parseInt(System.getenv().getOrDefault("MAX_LOANS_PER_MEMBER", "5"));

    private final LoanMapper loanMapper;

    public MemberService(LoanMapper loanMapper) {
        this.loanMapper = loanMapper;
    }

    public List<Loan> getLoansByMemberId(String memberId) {
        return loanMapper.findByMemberId(memberId);
    }

    public boolean addLoan(Loan loan) {
        // check for too many loans for this member
        List<Loan> loans = loanMapper.findByMemberId(loan.memberId());
        if (loans.size() >= MAX_LOANS_PER_MEMBER) {
            throw new LoanRejectedException("Member has too many loans");
        }
        return loanMapper.addLoan(loan);
    }
}   
