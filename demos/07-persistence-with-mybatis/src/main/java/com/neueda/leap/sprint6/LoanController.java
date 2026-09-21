package com.neueda.leap.sprint6;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController
// Normally we would have a service layer between the controller and the mapper
// and a different controller would be used for different resources. 
// e.g. members, resources, loans, etc.  This would allow the RequestMapping
// annotation to be applied at the class level for each specific resource type.
// @RequestMapping("resources")
public class LoanController {

    private final ResourceMapper resourceMapper;
    private final LoanMapper loanMapper;

    public LoanController(ResourceMapper resourceMapper, LoanMapper loanMapper) {
        this.resourceMapper = resourceMapper;
        this.loanMapper = loanMapper;
    }

    @GetMapping("/resources")
    public List<Resource> getResources() {
        return resourceMapper.findAll();
    }

    @GetMapping("/resources/{resourceId}")
    public ResponseEntity<Resource> getResource(@PathVariable String resourceId) {
        Resource resource = resourceMapper.findByResourceId(resourceId);
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resource);
    }

    @GetMapping("/members/{memberId}/loans")
    public List<Loan> getLoans(@PathVariable int memberId) {
        return loanMapper.findByMemberId(memberId);
    }
}
