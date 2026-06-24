# .NET Framework → Java 21 / Spring Boot mapping cheatsheet

These rules are authoritative. Apply the section matching the file's layer.
(Condensed for small-model use — keep this file under ~250 lines. Extend it
with project-specific decisions as they are made; it is the single source of truth.)

## Layer: batch (console apps, Windows services, Quartz/Hangfire, scheduled tasks)

The reference/existing batch job captured in `migration/java-conventions.md` is AUTHORITATIVE for exact API, bean wiring, and Spring Batch version — copy that shape. This table is only the conceptual mapping:

| .NET | Java (Spring Batch / scheduling) |
|---|---|
| Console app `static Main` doing a unit of work | one Spring Batch `Job` with one or more `Step`s |
| read source → transform → persist loop | chunk-oriented step: `ItemReader` → `ItemProcessor` → `ItemWriter` |
| single non-chunked action (cleanup, call, move file) | `Tasklet` step |
| Windows Service / `Timer` / `while(true){ ...; Sleep }` | `@Scheduled(cron=...)` triggering the job, OR an external scheduler launching it — match the reference |
| Quartz `IJob.Execute` | the job's trigger mechanism in the reference (`@Scheduled` or a launcher) |
| Hangfire `RecurringJob.AddOrUpdate` | scheduled trigger per reference |
| job arguments / config values | `JobParameters` (per reference) — not hard-coded |
| manual transaction around the batch | step-level chunk transaction (`PlatformTransactionManager`) per reference |
| restart / "skip bad row & continue" logic | skip/retry policy on the step per reference; do NOT silently drop the logic |
| progress logging / row counts | `StepExecutionListener` / `ItemWriteListener` per reference |

Rules: do NOT translate a batch job into a plain `@Service` with a `for` loop unless the reference does exactly that. Preserve the read-process-write boundaries and the commit/chunk semantics — getting chunk size or transaction boundaries wrong changes restart and memory behaviour. Keep all business logic (filtering, ordering, dedup, rounding) identical. Flag anything the reference doesn't cover with `// TODO-MIGRATION: batch — <reason>`.

## Layer: domain (POCO entities)

| C# (EF6) | Java (JPA / Spring Boot 3) |
|---|---|
| POCO class + DbSet registration | `@Entity` `@Table(name="...")` class |
| `[Key]` / Id convention | `@Id @GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `public string Name { get; set; }` | private field + getter/setter (or Lombok `@Getter @Setter` ONLY if project already uses Lombok) |
| `[Required]`, `[StringLength(n)]` | `@NotNull` / `@Column(nullable=false)`, `@Size(max=n)` + `@Column(length=n)` |
| `virtual ICollection<Order> Orders` | `@OneToMany(mappedBy="customer", fetch = FetchType.LAZY) List<Order>` |
| `virtual Customer Customer` + `CustomerId` | `@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="CustomerId")` — keep the ORIGINAL column name |
| `decimal` | `java.math.BigDecimal` — NEVER double/float for money |
| `DateTime` | `LocalDateTime`; `DateTimeOffset` → `OffsetDateTime`; `DateTime.UtcNow` → `Instant.now()` per project decision |
| `Guid` | `java.util.UUID` |
| nullable value types `int?` | `Integer`, `Long`, `Boolean` (boxed) |
| `enum` stored as int | `@Enumerated(EnumType.ORDINAL)` — verify against DB before choosing STRING |

Column/table names must match the EXISTING SQL Server schema (EF6 conventions: table = pluralized class name unless `[Table]` says otherwise). Always write explicit `@Table`/`@Column` names — do not rely on Hibernate naming strategy.

## Layer: data (DbContext, queries)

| C# (EF6) | Java |
|---|---|
| `DbContext` + `DbSet<T>` | one Spring Data `JpaRepository<T, ID>` interface per aggregate |
| `ctx.Customers.Where(c => c.Active)` | derived query `findByActiveTrue()` or `@Query` JPQL |
| `.Include(c => c.Orders)` | `@EntityGraph(attributePaths = "orders")` or JPQL `JOIN FETCH` |
| `.AsNoTracking()` read | plain query method (Spring Data is detached per tx end); add `@Transactional(readOnly = true)` on the service method |
| `FirstOrDefault()` | `Optional<T>` return — caller handles empty |
| `SingleOrDefault()` | `Optional<T>` + expect-one semantics; document if uniqueness assumed |
| `SqlQuery<T>("...")` | `@Query(value = "...", nativeQuery = true)` with named parameters — keep SQL byte-for-byte unless it uses EF parameter syntax |
| `SaveChanges()` | implicit at `@Transactional` commit; explicit `repository.save(e)` for new/detached entities |
| `Database.BeginTransaction()` | `@Transactional` on the service method |
| EF6 Migrations | Flyway scripts in `db/migration` — but for migration of an EXISTING DB, baseline only; do not regenerate schema |
| stored proc via `ExecuteSqlCommand` | `@Procedure` or JdbcTemplate — flag with TODO-MIGRATION for review |

LINQ translation rules: `Select` → `map`/JPQL projection, `Where` → predicate, `OrderBy/ThenBy` → `Sort`/`ORDER BY`, `GroupBy` → JPQL `GROUP BY` (do NOT translate to in-memory streams if the C# ran it in SQL — that changes performance semantics).

## Layer: service

- `async Task<T>` methods → plain synchronous `T` methods. Spring Boot 3.x on Java 21 uses virtual threads (`spring.threads.virtual.enabled=true`); do NOT introduce CompletableFuture/Reactor to mimic async/await.
- `.ConfigureAwait(false)`, `.Result`, `.Wait()` → delete; the call becomes direct.
- Constructor injection stays constructor injection: one `@Service` class, final fields, single constructor (no `@Autowired` needed).
- C# events/delegates → Spring `ApplicationEventPublisher` or a plain interface callback — flag with TODO-MIGRATION.
- `IDisposable`/`using` → try-with-resources only if the Java type is `AutoCloseable`; EF context disposal disappears (container-managed).

## Layer: web (controllers)

| C# | Java |
|---|---|
| `ApiController` / `Controller` returning JSON | `@RestController` + `@RequestMapping("<same route>")` |
| `[HttpGet("route")]`, `[HttpPost]` | `@GetMapping`, `@PostMapping` — preserve the EXACT route template and casing |
| `[FromBody] Dto dto` | `@RequestBody @Valid Dto dto` |
| `[FromUri]` / query params | `@RequestParam` (required flag must match C# optionality) |
| route `{id:int}` | `@PathVariable int id` |
| `Ok(x)` / `NotFound()` / `BadRequest(ms)` | `ResponseEntity.ok(x)` / `.notFound().build()` / `.badRequest().body(...)` — preserve status codes exactly |
| `ModelState.IsValid` | Bean Validation `@Valid` + `MethodArgumentNotValidException` handler → RFC 9457 `ProblemDetail` |
| MVC Controller returning Views | DO NOT translate Razor; expose the same data as a REST endpoint and mark `// TODO-MIGRATION: UI` |
| ViewModel / DTO classes | Java `record` |
| `[Authorize(Roles="X")]` | `@PreAuthorize("hasRole('X')")` — flag auth files for human review |
| HttpModules / ActionFilters | Spring `HandlerInterceptor` / `@ControllerAdvice` |

JSON contract: .NET default was PascalCase (or camelCase if JSON.NET configured). Check one existing response DTO; set Jackson `PropertyNamingStrategies` ONCE in config so property names on the wire DO NOT change.

## Layer: config

- `Web.config appSettings/connectionStrings` → `application.yml`; secrets → environment variables.
- DI registrations (Autofac/Unity/Ninject modules) → component scanning; only translate non-trivial registrations (named instances, decorators) explicitly with `@Bean` methods.
- `Global.asax` / OWIN `Startup` pipeline → `@Configuration` classes; order-sensitive middleware → flag TODO-MIGRATION.

## Semantic traps — always check

1. C# `==` on strings is value equality → Java `.equals()` (null-safe: `Objects.equals`).
2. C# `decimal` arithmetic → `BigDecimal` with explicit scale/rounding (`RoundingMode.HALF_EVEN` matches .NET banker's rounding for `Math.Round` defaults — verify per call site).
3. Integer division and `%` on negatives behave the same — but C# `Math.Round(double)` ≠ `Math.round` in Java (banker's vs half-up). Flag every rounding call.
4. `string.IsNullOrEmpty` → `s == null || s.isEmpty()`; `IsNullOrWhiteSpace` → `s == null || s.isBlank()`.
5. C# default `DateTime` is `MinValue`, Java reference is `null` — null-check semantics differ in persisted columns.
6. `foreach` over a modified collection throws in both — but LINQ deferred execution has NO Java equivalent; materialize where the C# enumerated lazily over changing data and flag it.
7. C# string interpolation `$"{x:N2}"` format specifiers → `String.format` with the correct Locale (default to `Locale.ROOT` unless UI-facing).
8. Exceptions: do not map to checked exceptions; use runtime exceptions + `@ControllerAdvice`.

## Project conventions (fill these in once, then they are law)
- Base package: `com.<company>.<app>`
- Build: Gradle / Maven: ______
- Lombok: yes / no: ______
- JSON casing on the wire: ______
- DB naming: keep SQL Server names verbatim: yes
