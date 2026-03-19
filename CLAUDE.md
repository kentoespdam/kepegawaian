# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build
./gradlew build

# Run (development profile)
./gradlew bootRun

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "id.perumdamts.kepegawaian.SomeTest"

# Clean build
./gradlew clean build
```

Requires Java 21. Uses Spring Boot 3.5.6 with Gradle.

## Architecture

This is an employee management (kepegawaian) system for PERUMDAMTS, a Spring Boot REST API.

**Stack:** MariaDB (JPA/Hibernate), Redis (cache), Kafka (payroll events), Appwrite (JWT auth), Spring Data Envers (audit history).

**Source root:** `src/main/java/id/perumdamts/kepegawaian/`

### Layered Structure

Controllers → Services → Repositories → Entities. All under the source root, organized by domain:
- `profil/` - Employee personal data (biodata, education, skills, family, training)
- `pegawai/` - Core employee records (keyed by NIPAM)
- `master/` - Reference data (organisasi, jabatan, golongan, grade, level, profesi)
- `cuti/` - Leave management with multi-level approval workflow
- `kepegawaian/` - Employee affairs (SK, SP, mutasi, kontrak, terminasi)
- `penggajian/` - Payroll with batch processing

### Key Patterns

**Response wrappers:** All controllers return via `CustomResult.any/list/save/delete()` which wraps responses in `{status, statusText, data, timestamp}`. DTO types: `PageResult`, `ListResult`, `SingleResult`, `SavedResult`, `ErrorResult`.

**Controller convention:** Standard CRUD endpoints with `@Valid` + `Errors` parameter for validation. Mutating endpoints use `@PreAuthorize("hasRole('ADMIN')")`. Paginated list via `@ParameterObject` request DTOs.

**Soft delete:** Entities use `is_deleted` flag — never hard-delete records.

**Audit:** All entities track `created_at/by`, `updated_at/by` via JPA `AuditAware`. Envers provides full revision history.

**Approval workflows:** Used in cuti (leave) and profil updates — multi-level chain with PENDING/APPROVED/REJECTED status.

**Entity ID pattern:** Most entities use `Long` auto-generated IDs. Exception: `Biodata` uses `NIK` (String) as primary key.

### Auth

JWT tokens from Appwrite validated by `JwtAuthFilter`. In `development` profile with no token, a hardcoded admin user is injected — no Appwrite needed for local dev.

### Configuration

`application.yml` reads all settings from environment variables (DB_HOST, APPWRITE_*, REDIS_*, KAFKA_*, etc.). Docker configs in `docker/` directory with `development` and `production` profiles.
