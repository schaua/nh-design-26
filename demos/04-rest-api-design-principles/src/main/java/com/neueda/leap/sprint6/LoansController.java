package com.neueda.leap.sprint6;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// Deliberately named for the RESOURCE (/loans), not an action. Every
// operation is expressed as a noun + an HTTP verb, never a verb in the URL.
// This is an in-memory demo, deliberately - the point today is HTTP
// semantics (verbs, status codes, idempotency), not persistence.
@RestController
@RequestMapping("/loans")
public class LoansController {

    private final Map<String, LoanRecord> store = new ConcurrentHashMap<>();
    private final AtomicInteger idSequence = new AtomicInteger(1);

    // GET /loans - read the whole collection. Safe: never changes state.
    @GetMapping
    public Collection<LoanRecord> getAll() {
        return store.values();
    }

    // GET /loans/{id} - read one resource. 200 if found, 404 if not -
    // "not found" is not a 200 with an error flag in the body.
    @GetMapping("/{id}")
    public ResponseEntity<LoanRecord> getOne(@PathVariable String id) {
        LoanRecord loan = store.get(id);
        if (loan == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(loan);
    }

    // POST /loans - create a new resource. 201 Created, with a Location
    // header pointing at the new resource's own URL. NOT idempotent - two
    // identical POSTs create two different loans, which is correct: the
    // client is asking to create something new each time.
    @PostMapping
    public ResponseEntity<LoanRecord> create(@RequestBody LoanRecord incoming) {
        String id = String.valueOf(idSequence.getAndIncrement());
        LoanRecord created = new LoanRecord(id, incoming.ISBN(), incoming.member());
        store.put(id, created);

        URI location = URI.create("/loans/" + id);
        return ResponseEntity.created(location).body(created);
    }

    // DELETE /loans/{id} - remove a resource. 204 No Content on success.
    // Idempotent: deleting an already-deleted (or never-existing) id still
    // returns a clean 404, not a 500 - calling DELETE twice never crashes.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!store.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        store.remove(id);
        return ResponseEntity.noContent().build();
    }
}
