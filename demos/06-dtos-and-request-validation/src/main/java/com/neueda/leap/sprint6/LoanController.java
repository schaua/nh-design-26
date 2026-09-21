package com.neueda.leap.sprint6;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService service;
    private final AtomicInteger idSequence = new AtomicInteger(1);

    public LoanController(LoanService service) {
        this.service = service;
    }

    // @Valid triggers Bean Validation against every annotated field on
    // loanRequestDto BEFORE this method body ever runs. A request that
    // fails validation never reaches this code at all - Spring intercepts
    // it and returns a 400 automatically.
    @PostMapping
    public ResponseEntity<LoanResponseDto> submitloan(@Valid @RequestBody LoanRequestDto request) {

        LocalDate dueDate = LocalDate.now().plusWeeks(2);
        String ISBN = request.ISBN();
        String resourceType = request.resourceType().name();
        String memberId = request.memberId();
        String id = String.valueOf(idSequence.getAndIncrement());
        String reason = "";
        ResponseEntity<LoanResponseDto> response;
        if (!service.isAvailable(ISBN)) {
            reason = "Resource not available";
            dueDate = null;
            response = ResponseEntity.badRequest().build();
        }
        else {
        LoanResponseDto body = new LoanResponseDto(ISBN, resourceType, memberId, dueDate, reason);
        URI location = URI.create("/loans/" + id);      
        response = ResponseEntity.created(location).body(body);
        }
        return response;
    }
}
