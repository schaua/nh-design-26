# Module 3 Demo Guide — Layered Architecture in Spring Boot

Run the app first, then walk through the layers top to bottom.

```bash
mvn spring-boot:run
curl http://localhost:8080/resources/M001
curl http://localhost:8080/resources/M002
curl http://localhost:8080/resources/UNKNOWN     # watch this one fail
```

Expect the first two to return a member resource count value with a timestamp; the third to return a raw
`500` with a stack trace in the console.

## The Three Layers, and Why Each One Exists

Walk through `ResourcesController` → `ResourcesService` → `ResourcesRepository` in that order,
naming each layer's ONE job:

- **Controller** — translates HTTP into a method call, and a return value back into HTTP.
  Nothing else. `ResourcesController` has zero business logic; it delegates to
  `ResourcesService` on the very next line.
- **Service** — business logic, coordinating whatever it needs from the repository layer.
  `ResourcesService` doesn't know or care that `ResourcesRepository` is currently a hardcoded
  `Map` — Module 7 swaps that for real Postgres access, and this class does not change.
- **Repository** — knows how to fetch data, and nothing else. `ResourcesRepository` is an
  interface, exactly like Sprint 5 Module 8's `ReportWriter` — the same Dependency Inversion
  pattern, applied to persistence this time.

**Each layer depends only on the layer directly below it.** The controller never talks to the
repository directly — that's a rule worth stating explicitly, not just an accident of how this
example was written.

## Constructor Injection, Concretely

Point at `ResourcesController`'s constructor and its parameter `ResourcesService`.  This parameter is not build with `new` anywhere in this class.  Spring:    
1. Sees `ResourcesService` is `@Service`-annotated → creates one. 

Point at `ResourcesService`'s constructor: `ResourcesRepository repository, Clock clock`. Neither
parameter is built with `new` anywhere in this class. Spring:

1. Sees `InMemoryResourcesRepository` is `@Repository`-annotated → creates one
2. Sees `AppConfig.clock()` is a `@Bean` method → calls it, gets a `Clock`
3. Sees `ResourcesService` has exactly one constructor needing a `ResourcesRepository` and a
   `Clock` → supplies both automatically when it creates the `ResourcesService` bean
4. The service is then supplied to `ResourcesController`

No XML, no manual wiring code anywhere — this whole chain is inferred from annotations and
constructor signatures.

## `AppConfig` — Not Every Bean Comes From a Stereotype

`Clock` is a JDK class — you can't put `@Component` on it, you don't own the source. A
`@Configuration` class with an `@Bean` method is how you register a bean for anything you didn't
write yourself, or want to configure explicitly (a specific `Clock`, a specific timeout, a
specific connection pool later in the sprint).

**On bean scopes**, briefly: every bean here is a **singleton** by default — one instance,
shared by everything that needs it, for the whole application's lifetime. That's almost always
what you want for stateless services and repositories. (`@Scope("prototype")` exists for the rare
case you want a new instance every time — worth naming, not worth dwelling on today.)

## The Actual Payoff: `ResourcesServiceTest`

This is the module's real point, not just a nice-to-have. Run:

```bash
mvn test
```

Walk through the test: **no Spring context starts, no embedded Tomcat, no real repository** —
just two Mockito mocks (`ResourcesRepository`, `Clock`) and a direct `new ResourcesService(...)`
call. Compare how long this test takes (milliseconds) to how long `mvn spring-boot:run` takes to
actually start the whole application.

**Say explicitly**: this is only possible *because* of the layering and constructor injection.
If `ResourcesService` built its own `InMemoryResourcesRepository` internally (the way
`BadOrderExecutor` did back in Sprint 5, Module 8), there would be no way to substitute a mock —
you'd be forced to either run the whole app or accept whatever the real repository does. This is
Sprint 5 Module 11's isolation lesson again, now inside a real framework.

## The Failing `/Resourcess/UNKNOWN` Request Is Deliberate

Don't apologise for the raw `500` — call it out as the current, honest state of the service.
There's no error handling yet; `NoSuchElementException` propagates all the way up to Spring's
default error page. **Module 10 fixes this properly.** For now, it's worth noting that a
service with clean layering still needs deliberate error handling on top — layering alone
doesn't give you good error responses for free.

## Transition to the Lab

Learners build the same three-layer structure themselves — `OrderController` →
`OrderService` → `OrderRepository` — for a different piece of the mission (a fee lookup), with a
pre-written Mockito test defining what `OrderService`'s behaviour should be.
