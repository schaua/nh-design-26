package com.neueda.leap.sprint6;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService service;
    private final Map<String, LoanResponseDto> loans = new ConcurrentHashMap<>();
    private final AtomicInteger idSequence = new AtomicInteger(1);

    public LoanController(LoanService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<LoanResponseDto> requestCheckoutResourceEntity(@Valid @RequestBody LoanRequestDto request) {
        String resourceId = request.resourceId();
        String memberId = request.memberId();
        boolean isWithinMaxLoans = service.isWithinMaxLoans(resourceId, memberId);

        String id = String.valueOf(idSequence.getAndIncrement());
        LocalDate dueDate = LocalDate.now().plusWeeks(2);
        String reason = isWithinMaxLoans ? null : "Exceeded max loans"; 
        LoanResponseDto response = new LoanResponseDto(id, isWithinMaxLoans ? "ACCEPTED" : "REJECTED", dueDate, reason);
        loans.put(id, response);

        URI location = URI.create("/loans/" + id);
        return ResponseEntity.created(location).body(response);
    }

    // No manual null-check + notFound().build() here any more - throwing
    // lets GlobalExceptionHandler produce the SAME error shape this
    // endpoint would get from an unknown ticker three layers down in
    // loanservice. One handler, every "doesn't exist" case in the service.
    @GetMapping("/{id}")
    public ResponseEntity<LoanResponseDto> getOrder(@PathVariable String id) {
        LoanResponseDto order = loans.get(id);
        if (order == null) {
            throw new NoSuchElementException("no order with id " + id);
        }
        return ResponseEntity.ok(order);
    }
}
