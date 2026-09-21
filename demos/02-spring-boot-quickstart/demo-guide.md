# Module 2 Demo Guide — Spring Boot Quickstart

Live-build this from an empty folder if time allows — otherwise walk through the finished
project in this order, as if building it.

```bash
mvn spring-boot:run
# in a second terminal:
curl http://localhost:8080/hello
```

Expect: `Hello from the mission service`.

## Step 0: REST, at the Bare Minimum

Before any code, make sure everyone has the minimum vocabulary `@GetMapping` needs to make
sense — this is deliberately shallow, Module 4 covers it properly:

- A **client** (a browser, `curl`, another service) sends an **HTTP request** to a **URL**
- The request has a **method** (verb) saying what kind of action it is — today, just `GET`:
  "give me this, don't change anything"
- The **server** processes it and sends back an **HTTP response**, with a **status code**
  (`200` means "it worked") and a **body** (the actual data)

That's the whole model: request in, response out. `GET /hello` → `200 OK` with the greeting as
the body. Everything else about REST (other verbs, resource design, versioning) is Module 4 —
today you only need enough to understand what `@GetMapping("/hello")` is doing.

## Step 1: `pom.xml` — the Parent Is Doing Most of the Work

- Create the project structure    
```
   Module02
   |- src
   |    |- main
   |    |   |- java
   |    |   |     |- com
   |    |   |         |- neueda
   |    |   |             |- leap
   |    |   |                 |- sprint6
   |    |   |- resource
   |    |
   |    |- test
   |        |- java
   |        |    |- com
   |        |        |- neueda
   |        |            |- leap
   |        |                |- sprint6
   |- pom.xml
```
- Edit `pom.xml`    
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0">
  <modelVersion>4.0.0</modelVersion>

  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.4</version>
    <relativePath/>
  </parent>

  <groupId>com.neueda.leap</groupId>
  <artifactId>sprint6-m02-demo</artifactId>
  <version>0.1.0</version>
  <packaging>jar</packaging>

  <properties>
    <java.version>21</java.version>
  </properties>
  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
    </dependency>
  </dependencies>
  <build>
    <finalName>sprint6-m02-demo</finalName>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>
</project>
```
Point at `<parent>spring-boot-starter-parent</parent>` first. This is a **dependency management
BOM (Bill of Materials)** — it doesn't add any dependencies itself, but it pins compatible
versions for every Spring Boot dependency you add later, so you never have to figure out which
version of `spring-web` matches which version of `spring-context` by hand.

Then point at the single dependency: `spring-boot-starter-web`. **Say explicitly**: this one
"starter" transitively pulls in Spring MVC, an embedded Tomcat server, and Jackson (for JSON) —
compare this to a plain Java web app, where you'd configure a servlet container, a dispatcher
servlet, and a JSON library by hand.

## Step 2: `MissionServiceApplication` — What `@SpringBootApplication` Actually Does

Add `MissionServiceApplication.java` to the sprint6 src folder.  
Edit `MissioneServiceApplication.java`    
```java
package com.neueda.leap.sprint6;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication is three annotations in one:
// - @Configuration: this class can define Spring beans
// - @EnableAutoConfiguration: Spring Boot configures things for you based on
//   what's on the classpath (spring-boot-starter-web on the classpath -> an
//   embedded Tomcat gets configured automatically, no XML, no manual wiring)
// - @ComponentScan: Spring looks for @Controller, @Service, etc. in this
//   package and everything under it
@SpringBootApplication
public class MissionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MissionServiceApplication.class, args);
    }
}
```

This one annotation is three:
- `@Configuration` — this class can define Spring beans
- `@EnableAutoConfiguration` — Spring Boot looks at what's on the classpath and configures things
  automatically (web starter present → configure an embedded Tomcat, no XML, no manual wiring)
- `@ComponentScan` — Spring scans this package and everything under it for `@Controller`,
  `@Service`, and other Spring-managed classes

**The `main` method really is that short.** `SpringApplication.run(...)` starts the whole
application context, the embedded server, and auto-configuration in one call.

## Step 3: `HelloController` — the Fastest Possible Endpoint

Add `HelloController.java` to the sprint6 src folder.  
Edit `HelloController.java`  
```java
package com.neueda.leap.sprint6;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// @RestController = @Controller + @ResponseBody: every method's return value
// is written directly to the HTTP response body (as JSON, if it's an object),
// rather than being resolved to a view template.
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from the mission service";
    }
}
```
`@RestController` = `@Controller` + `@ResponseBody`: every method's return value is written
directly to the response body, not resolved to a view template. `@GetMapping("/hello")` maps
`GET /hello` to this method. Returning a plain `String` here is deliberate — the response is
`text/plain`, not JSON, because there's no object to serialize yet. That changes in Module 6
when DTOs show up.

## Step 3.5: Dependency Injection — Spring's Core Idea

Don't let this module end without naming this explicitly, even though today's code has nothing
injected yet.

**Normally in Java**, if class A needs class B, A creates it itself: `new B()`. That ties A
directly to one specific implementation of B, forever, unless someone edits A's code.

**Spring flips this.** Spring creates the objects it manages (called **beans**) and *hands* them
to whatever needs them — you never write `new` for a Spring-managed class yourself. This is
**Inversion of Control**: instead of your code reaching out and constructing its own
dependencies, the framework constructs them and pushes them in. "Don't call us, we'll call you."

**Point at what's already happening today, even without an injected dependency**:
`@ComponentScan` found `HelloController`. Spring created it. You never wrote
`new HelloController()` anywhere — that instantiation is dependency injection already at work,
even with zero constructor parameters.

**Preview Module 3 explicitly**: the next module adds a `Service` class and injects it into a
`Controller`'s constructor — no `new` anywhere, Spring wires the two together automatically.

**Tie it back to Sprint 5**: this is Module 8's Dependency Inversion Principle
(`OrderExecutor(ReportWriter writer)` — a constructor taking an abstraction, not building a
concrete class itself), except now the framework does the wiring for you instead of you writing
`new OrderExecutor(new ConsoleReportWriter())` by hand. Same idea, automated.

## Step 4: `application.properties` — Configuration, Not Code

Add `application.properties` to the resources folder.    
Edit `application.properties`    
```
server.port=8080
spring.application.name=mission-service
```
`server.port=8080` and `spring.application.name=mission-service` are both plain key-value
config, read at startup — no annotations, no code changes needed to alter them. Point out: this
is where Module 9's JWT secret and Module 7's database connection details will eventually live
too, though never as plain text in a real system (that's its own future conversation).    

Run the application in one terminal.    
```sh
mvn spring-boot:run
```

Test the application from a second terminal.    
```sh
curl http://localhost:8080/hello
```

## Points to Make Explicitly

- **This is the fastest possible confidence-building win, deliberately.** No persistence, no
  security, no business logic yet — just "does the service start, does an endpoint respond."
  Everything harder gets layered on top of this working skeleton, one module at a time.
- **Auto-configuration is convenience, not magic.** Everything Spring Boot configures
  automatically here could be configured by hand — the framework is making a reasonable default
  choice based on what's on the classpath, not doing anything it couldn't be told to do explicitly.

## Transition to the Lab

Learners bootstrap their own first Spring Boot service from scratch and get their own `hello`
endpoint running — the same fast win, built by their own hands this time.  No automated scaffolding from IntelliJ or start.spring.io.
