package com.neueda.leap.sprint6;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {

    private final LoanService loanService;

    public MemberController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping("/members/{memberId}/loans")
    public List<Loan> getLoans(@PathVariable String memberId) {
        return loanService.getLoans(memberId);
    }    
}
