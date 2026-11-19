# Project Rules for AI Agents: kepegawaian (Antigravity 2.0 Optimized)

## Konteks Project

Project **kepegawaian** adalah backend sistem kepegawaian PDAM (Perusahaan Daerah Air Minum). Stack: **Java + Spring Boot 4.0.3**, **jOOQ**, **Flyway 12**, **MariaDB**, **Redis**, **Appwrite JWT**. Build tool: **Gradle (Kotlin DSL)**.
Referensi utama: [`CLAUDE.md`](./CLAUDE.md), [`CODING_RULES.md`](./CODING_RULES.md), [`CONTEXT-MAP.md`](./CONTEXT-MAP.md), [`CONTEXT.md`](./CONTEXT.md).

Domain utama: `profil/`, `pegawai/`, `master/`, `cuti/`, `kepegawaian/`, `penggajian/`.

---

## 1. Adaptive Routing & Agent Roles (Direct-First)

Apply this strict priority order before executing any task:

### Priority 1: Mandatory Deep-Reasoning (`deep_reasoner` Sub-Agent)
Invoke `invoke_subagent` with `deep_reasoner` (tier: `pro`) BEFORE writing code when ANY of these objective conditions is met:
- **Security/Auth:** Menyentuh `JwtAuthFilter`, `SecurityConfig`, atau logika `@PreAuthorize` — SELALU.
- **Payroll/Penggajian:** Mengubah formula gaji, komponen tunjangan, atau batch `penggajian/`.
- **Interface Break:** Refactor yang mengubah signature publik `CustomResult`, `PagedRequest`, atau kontrak controller antar-modul.
- **DB Migration:** Menulis atau mengubah skrip Flyway (`V*.sql`) yang melibatkan DDL kritis (DROP, ALTER pada tabel live).
- **Stuck Bug:** Bug tidak terjelaskan setelah tepat 2 attempt debugging di Root Agent.

*Exceptions (Do NOT invoke `deep_reasoner`):*
- Implementing an already-confirmed Implementation Contract from a prior `deep_reasoner` response.
- Writing routine unit tests (`*Test`, `*ControllerTest`) or fixing localized bugs with an already-identified root cause.
- Task yang tidak menyentuh `auth/`, `penggajian/`, atau kontrak publik antar-modul sama sekali.

**Quota Fallback:** Jika `deep_reasoner` (model: `pro`) mengembalikan error quota/rate-limit, WAJIB segera re-invoke dengan `deep_reasoner_fallback` (model: `flash`). Gunakan prompt dan context identik. Jika fallback `Confidence Level: LOW`, tunda implementasi dan laporkan ke user.

### Priority 2: Direct Execution (Root Agent — No Sub-Agent)
Root Agent MUST execute directly without spawning any sub-agent when:
- Running issue tracker operations (`bd`), `git` commands, or single `gitnexus` queries.
- Making localized code edits (≤ 2 files, < 50 LOC) following established patterns in `CODING_RULES.md`.
- Running final verification commands (`./gradlew test`, `./gradlew compileJava`).
- Responding to questions, writing docs, or analysis without code changes.

### Priority 3: Substantial Technical Delegation (`flash` Sub-Agent)
Invoke `invoke_subagent` with model `flash` when:
- Implementation spans ≥ 2 files with substantial changes (new endpoints, new modules).
- Creating a test suite with ≥ 3 test cases.
- Broad codebase exploration requiring reads across > 5 files.
- Any task estimated to take > 15 minutes of execution time.

---

## 2. Sub-Agent Execution, Error Handling & Reporting Protocol

### Bounded Self-Fix (Anti Ping-Pong)
- Sub-agent DIIZINKAN melakukan perbaikan mandiri (*self-fix*) maksimal **2 kali percobaan lokal** khusus untuk error deterministik (syntax error, import hilang, kegagalan `compileJava`, atau kegagalan test dengan penyebab jelas).
- Jika masih gagal setelah 2 percobaan, atau jika menemukan ambiguitas desain/pelanggaran constraint, sub-agent WAJIB berhenti dan melapor ke Root Agent.
- Jika error berlanjut setelah 3 percobaan di Root Agent, lakukan riset `CODING_RULES.md` atau dokumentasi Spring/jOOQ terbaru.

### Proportional Reporting Protocol (STRICT)

**A. Untuk Tugas Eksplorasi / Riset (Tanpa Modifikasi File):**
Laporkan temuan secara padat dalam maksimal 5 baris berisi poin inti dan referensi `file:baris`. Akhiri dengan: `"Report complete. Awaiting next instructions."`

**B. Untuk Tugas Modifikasi Kode / Eksekusi Teknis:**
Sub-agent wajib melaporkan dalam format berlapis berikut:

```text
STATUS: [DONE|PARTIAL|BLOCKED|ERROR]
HASIL: <1–3 kalimat inti — apa yang berubah, ditemukan, atau gagal, dan mengapa penting>

ARTEFAK TERMODIFIKASI:
- <file>:<baris-dari>–<baris-ke> — <deskripsi singkat perubahan>
  CONSTRAINT CHECK: <PASS/FAIL + alasan singkat terhadap: soft-delete, audit fields, TTL Redis, Flyway atomicity>

VERIFIKASI:
- Tests: <X passed, Y failed — sebutkan nama test spesifik yang relevan; nyatakan eksplisit jika 0 test>
- Build: <PASS / FAIL: <baris pesan error spesifik dari compileJava/bootJar>>
- Regression: <Ya/Tidak + detail singkat>

Report complete. Awaiting next instructions.
```

**Aturan Ketat Laporan Modifikasi:**
- **Wajib sebutkan `file:baris`** untuk setiap perubahan dan **wajib jalankan Constraint Check**.
- Buang *stack trace* penuh atau listing kode panjang — ringkas hanya ke baris error spesifik (< 10 baris raw output).
- DILARANG menyertakan saran, opini, langkah selanjutnya, atau mengeksekusi pekerjaan di luar scope.
- DILARANG melaporkan `STATUS: DONE` jika ada test `FAIL` atau build `FAIL`.
- Root Agent WAJIB memeriksa bagian `ARTEFAK TERMODIFIKASI` dan `VERIFIKASI` sebelum mengambil keputusan berikutnya.

---

## 3. Token Economy & Pre-Coding Gates

- **No Preamble & Concise Output:** Hilangkan kalimat basa-basi ("Baik", "Tentu saja", "Saya akan..."). Mulai langsung dari substansi.
- **Selective Reading & Micro-Diffs:** Baca hanya rentang baris yang relevan. Edit hanya baris yang berubah.
- **Quiet Commands:** Selalu gunakan flag minim output (`--quiet`, `head`, `grep`).
- **Domain Context Gate:** Sebelum menyentuh modul baru, baca konteks relevan dari `CONTEXT-MAP.md`:
  - `master/` → `docs/context/language-master.md`
  - `pegawai/` → `docs/context/language-pegawai.md` + `decisions-pegawai.md`
  - `profil/` → `docs/context/language-profil.md`
  - `cuti/` → `docs/context/language-cuti.md` + `decisions-cuti.md`
  - Auth/JWT → `docs/context/language-security.md`
- **GitNexus Targeted Gate:**
  - WAJIB gunakan `gitnexus_impact` sebelum memodifikasi symbol apa pun.
  - Untuk CLI: selalu sertakan `--repo kepegawaian` (contoh: `npx gitnexus query "CustomResult" --repo kepegawaian`).
- **Conditional Skill Gate:**
  - **Ponytail:** WAJIB load skill `/ponytail` sebelum memodifikasi kode produksi.
  - **Grilling:** WAJIB ikuti skill `grilling` saat melakukan Q&A arsitektur atau desain baru.
- **Issue Tracking (Beads/`bd`):** Klaim task (`bd update <id> --claim`), tutup task (`bd close <id>`).
- **Sandbox Policy:** WAJIB gunakan `BypassSandbox: true` untuk semua eksekusi perintah `git` dan `bd`.

---

## 4. File Size, Java Toolchain & Domain Rules

### Modularitas & Ukuran File
- Target: **150–250 LOC** per file.
- Hard Ceiling: **Maksimal 300 LOC**. WAJIB pecah/modularisasi jika mendekati batas.
- Struktur modul: pisahkan per domain → `controller/`, `service/`, `repository/` (sub: `.jpa`, `.jooq`), `dto/`, `entity/`, `mapper/`.

### Java/Gradle Toolchain (Wajib)
Seluruh eksekusi WAJIB menggunakan Gradle wrapper:
```bash
./gradlew compileJava          # Kompilasi (cek syntax & import)
./gradlew test                 # Jalankan seluruh test
./gradlew test --tests "*.XxxTest"  # Jalankan test spesifik
./gradlew bootJar              # Build JAR produksi
make lint                      # Cek lint & formatting Spotless (./gradlew spotlessCheck)
make format                    # Format kode & bersihkan unused import otomatis (./gradlew spotlessApply)
make run-dev                   # Dev mode: continuous compile + Spring Boot DevTools
make start-dev                 # Jalankan Docker dev stack (MariaDB, Redis, dll)
```

### Domain & Coding Constraints (Harga Mati)
1. **Soft Delete Only:** JANGAN pernah hard-delete. Gunakan flag `is_deleted`. Semua query WAJIB filter `is_deleted = false`.
2. **Audit Fields:** Setiap entity WAJIB punya `created_at`, `created_by`, `updated_at`, `updated_by` via `AuditAware`. Jangan set manual.
3. **Controller Response:** SELALU gunakan `CustomResult.any()` / `CustomResult.list()` / `CustomResult.save()` / `CustomResult.delete()`. JANGAN return `@Entity` langsung.
4. **Repository Split:** `.jpa` untuk Spring Data JPA, `.jooq` untuk jOOQ DSLContext. JANGAN campur keduanya dalam satu class.
5. **Redis TTL Mandatory:** Setiap cache key WAJIB punya TTL. Konfigurasi per-cache di `RedisCacheManagerBuilderCustomizer`.
6. **Flyway Atomicity:** Satu migration file = satu perubahan logis. DDL kritis (DROP, ALTER) = satu statement per file. JANGAN batch DDL kritis.
7. **Security:** Semua endpoint mutating WAJIB `@PreAuthorize("hasRole('ADMIN')")`. Lambda DSL untuk `HttpSecurity` — tidak boleh menggunakan `.and()` chaining (removed di Spring Security 6+).
8. **Nominal Gaji:** SELALU ambil dari data server-side (entity `Penggajian`). Klien TIDAK BOLEH menentukan atau mengubah nilai komponen gaji.
9. **Spotless Lint & Format:** Seluruh file Java WAJIB bersih dari unused import dan mematuhi format Spotless (`make lint`). Jalankan `make format` sebelum commit.

---

## 5. Prosedur Penyelesaian Pekerjaan (Completion Workflow)

Sebelum menyatakan task selesai secara keseluruhan:
1. Jalankan `gitnexus_detect_changes()` untuk memverifikasi cakupan perubahan.
2. Jalankan `make format` atau `./gradlew spotlessApply` untuk memastikan formatting bersih & tanpa unused import.
3. Jalankan `make lint` atau `./gradlew spotlessCheck` — pastikan zero formatting/linting violations.
4. Jalankan `./gradlew test` — pastikan seluruh test yang relevan lulus (hijau).
5. Jalankan `./gradlew compileJava` — pastikan bebas error kompilasi.
6. Push perubahan ke remote (WAJIB dengan `BypassSandbox: true`):
   ```bash
   git pull --rebase
   bd dolt push
   git push
   git status  # HARUS menunjukkan "up to date with origin"
   ```
7. Perbarui atau tutup status issue di `bd` (`bd close <id>`).