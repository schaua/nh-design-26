# Module 7 Demo Guide — Persistence with MyBatis: Mappers & Connecting to Postgres

Everything before today has run on hardcoded data (`InMemoryLoansRepository`,
`InMemoryResourcesRepository`). Today the service talks to the real Sprint 3 Postgres schema — the
same `enterprise-schema.sql` learners already know from Sprint 3.

```bash
# Confirm the local mission database exists (created earlier in the sprint, from
# Sprint 3's enterprise-schema.sql) - see the lab README's Setup section if not

mvn spring-boot:run

curl http://localhost:8080/resources/9780062073562
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/resources/NOPE
curl http://localhost:8080/members/16/loans
```

Expect: real resource data for `AAPL`; a clean `404` for an unknown resourceId; a list of Alice
Johnson's loans, joined from three tables.

## Two Mappers, Two Styles — Side by Side

Open `ResourceMapper.java` and `LoanMapper.xml` next to each other.

**`ResourceMapper`** — annotation-based. The SQL lives in `@Select`, right on the method:

```java
@Select("SELECT resourceId, title, authorId, mediaId, available FROM resources WHERE resourceId = #{resourceId}")
Resource findByResourceId(String resourceId);
```

**`LoanMapper`** — XML-based. The interface (`LoanMapper.java`) declares only the method
signature; the SQL lives in a separate file, `LoanMapper.xml`, joining three tables:

```xml
<mapper namespace="com.neueda.leap.sprint6.LoanMapper">
    <!-- namespace above must exactly match the Java interface's fully
         qualified name - that's how MyBatis wires this XML to that
         interface. id below must match the method name. -->
    <select id="findByMemberId" resultType="com.neueda.leap.sprint6.Loan">
        SELECT
            l.loanId      AS loanId,
            m.memberId    AS memberId,
            m.name        AS memberName,
            r.title       AS resourceTitle,
            l.resourceId  AS resourceId,
            l.loanDate    AS loanDate
        FROM loans l
        JOIN members m    ON l.memberId = m.memberId
        JOIN resources r ON l.resourceId = r.resourceId
        WHERE m.memberId = #{memberId}
        ORDER BY loanDate
    </select>
</mapper>
```

**Ask the group**: why would you pick one over the other? Land on: annotations are fine for a
one-line, single-table query — there's nowhere else worth looking. Once there's a join and several
aliased columns, cramming it into a Java string annotation gets unreadable fast; XML lets the SQL
read like SQL.

**Point out the wiring, not just the syntax**: the `namespace` in `LoanMapper.xml` is the
Java interface's *fully qualified name*, and the `id` is the method name. That's the entire
connection between the two files — no annotation on the Java side at all. Get the namespace wrong
and MyBatis fails at startup with a clear "no statement found" error — worth deliberately breaking
it live if there's time, to show what that looks like.

## `#{resourceId}` Is Not String Concatenation

Point at `#{resourceId}` in the `@Select` annotation. This compiles to a JDBC `PreparedStatement` with
a bound parameter — the same SQL-injection protection Sprint 3 covered for raw SQL. Contrast
(don't demo) what `"...WHERE resourceId = '" + resourceId + "'"` would open up.

## `Resource`/`Loan`: Plain Classes, Not Entities

Open `Resource.java`. No `@Entity`, no `@Id`, no JPA annotations at all — just fields and
getters/setters. MyBatis populates it by matching column names (helped here by
`mybatis.configuration.map-underscore-to-camel-case=true` in `application.properties`, which turns
`asset_class` into `assetClass` automatically). There's no persistence context, no dirty-checking,
no entity lifecycle — the object exists only as the shape of one query's result. Module 8 picks
this contrast up directly against JPA/Hibernate.

## Transition to the Lab

Learners write one of each: an annotation-based `AdvisorMapper.findById` and the XML-based
`TransactionMapper.xml`, against the same live local Postgres database.
