# Architecture conventions — Sistema Odontológico (backend)

This doc captures the **non-obvious rules** the codebase already follows so
new features stay consistent. None of this is enforced by a linter — read it
before adding a new module.

## Stack

- Java 17 / Spring Boot 3.2
- PostgreSQL 16 + Flyway for schema versioning
- JPA / Hibernate 6 + Lombok + MapStruct
- JWT auth (jjwt 0.12.x), per-request `TenantContext` (ThreadLocal)
- Springdoc OpenAPI for `/swagger-ui`

## Package layout

```
com.bs.odontograma
├── shared/          → BaseEntity, ApiResponse, SecurityConfig, TenantContext, exceptions
├── auth/            → User + JWT + login/register
├── tenant/          → Tenant entity + self-registration
├── patient/         → Patients
├── treatment/       → Treatment catalog + Estimate + EstimateItem
├── appointment/     → Appointments
├── odontogram/      → Odontogram + ToothRecord + ToothSurfaceCondition + ToothHistoryEntry
├── audit/           → AuditLog
└── dashboard/       → KPI reads
```

Every feature module follows the same internal layout:

```
<feature>/
├── controller/      → @RestController, no business logic
├── service/         → @Service, @Transactional, owns the business rules
├── repository/      → Spring Data JPA interfaces
├── entity/          → JPA @Entity extending BaseEntity (when tenant-scoped)
├── dto/
│   ├── request/     → @Valid input DTOs (Lombok @Data)
│   └── response/    → output DTOs
├── mapper/          → MapStruct entity↔dto converters
└── enums/           → Java enums stored as VARCHAR in DB
```

Anything that crosses a controller boundary is a DTO. **Entities never leak out**
of the service layer — even read endpoints return `*Response` records.

## Multi-tenancy

Every per-clinic entity extends `BaseEntity`, which adds:

- `id UUID` (auto-generated in `@PrePersist`)
- `tenant_id UUID NOT NULL`
- `created_at`, `updated_at`, `created_by`, `updated_by` (audit)
- `version BIGINT` (optimistic lock)

The `tenant_id` is **explicitly set by each service** in its `create()` method
via `entity.setTenantId(tenantContext.getCurrentTenantId())`. We do NOT rely on
a JPA entity listener for this — listeners are instantiated by Hibernate (not
Spring) and the wiring is fragile. Setting the field explicitly is one extra
line per create method and removes a class of "null tenant_id" bugs.

`TenantContext.getCurrentTenantId()` reads from a ThreadLocal populated by
`JwtAuthenticationFilter` from the JWT claim, with a fallback to
`SecurityContextHolder → UserPrincipal.tenantId`. Always non-null for
authenticated requests.

## Repositories

Every query that returns tenant-scoped data **must take a `tenantId` parameter**
and include it in the WHERE clause. Spring Data names like
`findByTenantIdAndDocumentNumber`, `existsByTenantIdAndCode`, etc. There are no
"global" finders — leaking across tenants is the easiest way to ship a security
incident.

## Database migrations (Flyway)

```
src/main/resources/db/migration/
└── V<N>__<snake_case_description>.sql
```

Rules:
- **Never modify a migration that has been merged to main**. Add a new
  V(N+1) instead.
- Every tenant-scoped table has `tenant_id UUID NOT NULL` plus a FK to
  `tenants(id)` and an index on `tenant_id`.
- Enums are stored as `VARCHAR(<n>)`. **Avoid `CHECK (col IN (...))` constraints**
  on enum columns — adding a new enum value would otherwise require a Flyway
  migration. The compile-time Java enum is the source of truth.
- Boolean flags are `BOOLEAN DEFAULT TRUE`.
- Money is `NUMERIC(12, 2)` or larger — never floating point.

## DTO conventions

- **Request DTOs** use `@Valid` + `@NotBlank` / `@Email` / `@Size` and live in
  `<feature>/dto/request/`. They never carry server-generated fields
  (id, tenantId, createdAt).
- **Response DTOs** are flat. Include `id`, `tenantId` (for client routing),
  display fields (`fullName`, `treatmentName`) and timestamps as ISO strings.
- MapStruct mappers live in `<feature>/mapper/` and are invoked from services
  only.

## API surface

- Base path: `/api/<feature>`
- Public endpoints (no JWT required):
  - `POST /api/auth/login`
  - `POST /api/auth/refresh`
  - `POST /api/auth/bootstrap` (first install only)
  - `POST /api/tenants/register` (self-service onboarding)
  - `/api/health/**`, `/swagger-ui/**`, `/api-docs/**`
- Everything else needs `Authorization: Bearer <token>` and goes through
  `JwtAuthenticationFilter`.
- Authorization at the method level via `@PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")`.

All responses are wrapped in `ApiResponse<T>`:

```json
{ "success": true, "message": "...", "data": {...}, "timestamp": "..." }
```

## Catalogs are exposed as endpoints

Anything the frontend renders as a list/menu (tooth conditions, treatment
specialties, appointment statuses, …) should have a corresponding
`GET /api/<feature>/<catalog>` endpoint. This lets us move the catalog into a
DB table later **without breaking the API contract**.

Example: `GET /api/odontogram/conditions` returns the supported
`ToothCondition` values. Today they come from the Java enum; tomorrow they can
come from a `tooth_condition_catalog` table and the frontend won't notice.

## Extending — checklists

### Add a new enum value (e.g. a new `ToothCondition`)

1. Add the literal to the Java enum.
2. Confirm the DB column is `VARCHAR(N)` with no CHECK constraint
   (it usually is — see `db/migration/V6` for the pattern).
3. Add the label + category to the catalog controller
   (`ToothConditionCatalogController` for tooth conditions).
4. Update the frontend mirror: see `docs/ADDING_FINDINGS.md` on the front repo.

### Add a new feature module

1. Create the package tree (`controller / service / repository / entity / dto / mapper / enums`).
2. New entity extends `BaseEntity`.
3. New Flyway migration:
   - `tenant_id UUID NOT NULL`
   - FK to `tenants(id)`
   - index on `tenant_id`
   - audit columns
   - `version BIGINT`
4. Repository methods all take `tenantId`.
5. Service sets `tenantId` explicitly on create.
6. Controller wraps responses in `ApiResponse.success(...)`.
7. Add `@PreAuthorize` to every method.
