# Knowledge — kepegawaian (PERUMDAMTS)

> Autoload by Freebuff. Gabungan `CLAUDE.md`, `AGENTS.md`, `CODING_RULES.md`.

---

## 1. Project Identity

| Item | Value |
|------|-------|
| Nama | `kepegawaian` — Employee Management System |
| Entitas | PERUMDAMTS |
| Type | Spring Boot REST API |
| Stack | Java 25, Spring Boot 4.0.3, Gradle |
| DB | MariaDB (JPA/Hibernate + Envers) |
| Infra | Redis, Kafka, Appwrite (JWT) |
| Base pkg | `id.perumdamts.kepegawaian` |
| Branch | `rewrite/master-cqrs` |
| Legacy | `../kepegawaian-legacy` (tag `legacy-snapshot`, read-only) |

---

## 2. Modes of Operation

Freebuff beroperasi dalam **2 mode**. Mode ditentukan oleh prompt pertama user:

| Mode | Role | Output | Kapan? |
|------|------|--------|--------|
| **🔍 Grilling** | 🧠 **Manager** — analisis, rencana, NO CODING | Beads issue (plan implementasi) + MD file (checklist claim order) | User minta review/analisis/desain/planning |
| **💻 Coding** | 🔧 **Engineer** — eksekusi issue sesuai aturan | Code changes + update MD file + commit & push | User minta implementasi / ngerjain issue |

### 🔍 Grilling Mode

> **Agent sebagai Manager.** Tidak menulis kode sama sekali.

1. Analisis domain/modul yang diminta — baca CONTEXT, ADR, docs terkait
2. Grilling → sharpen plan bareng user (tanya-jawab)
3. Buat **beads issue** (`bd create`) berisi:
   - Judul: `{modul}: {deskripsi singkat}`
   - Body: implementasi plan langkah per langkah
   - Label: sesuai triage
4. Buat **MD file** di `docs/` berisi:
   - Claim order checklist (step-by-step urutan ngerjain)
   - Referensi context/docs yang relevan
   - Dependency/urut-urutan
5. Done — tidak perlu compile/test/push

### 💻 Coding Mode

> **Agent sebagai Engineer.** Mengeksekusi issue yang sudah ada plan-nya.

1. **Baca MD file** terkait issue — baca **teliti & mendalam**, pahami claim order
2. **Baca CONTEXT files** — `docs/context/language-{domain}.md`, ADR, dll. **JANGAN halu/tebak-nebak**
3. **Aktifkan `/ponytail`** — skill untuk memaksa solusi paling sederhana, minimal, YAGNI. **WAJIB** sebelum nulis kode.
4. **Kerjakan issue** — ikuti workflow coding di section 7
5. **Update MD file** — tandai step yang sudah selesai
6. **Close issue** — `bd close <id>`
7. **Commit & push** ke GitHub sebagai finalisasi

---

## 3. Build & Run

```bash
./gradlew build                # Build all
./gradlew bootRun              # Dev profile
./gradlew test                 # All tests
./gradlew clean compileJava    # Verify compile (post-git-mv sanity)
```

---

## 4. Architecture

### Domain Modules

| Package | Function |
|---------|----------|
| `profil/` | biodata, pendidikan, keahlian, keluarga, pelatihan |
| `pegawai/` | core employee records (NIPAM key) |
| `master/` | referensi (organisasi, jabatan, golongan, grade, level, profesi) |
| `cuti/` | leave, multi-level approval chain |
| `kepegawaian/` | SK, SP, mutasi, kontrak, terminasi |
| `penggajian/` | payroll, batch processing, gajiBatchRoot + Kafka |

### Layer Pattern (per Domain Module)

```
{domain}/           e.g. profil/, master/, pegawai/
├── controllers/    extends AController, @RequestMapping("/api/{domain}")
├── services/       @Transactional business logic → return CustomResult
├── repositories/   JPA (JpaRepository) or JOOQ (DSLContext) queries
├── dto/            request (@NotBlank, @Valid) + response (query mapping)
├── mapper/         entity ↔ DTO conversion
└── entities/       JPA @Entity (if using JPA path)
```

### Code Patterns

| Aspect | Rule |
|--------|------|
| Controller | `CustomResult.any/list/save/delete()` → `{status, statusText, data, timestamp}` |
| Validation | `@Valid` + `Errors` on all mutating endpoints |
| Auth mutating | `@PreAuthorize("hasRole('ADMIN')")` |
| Pagination | `@ParameterObject` on query params |
| Soft delete | `is_deleted` flag — **never hard-delete** |
| Audit | `created_at/by`, `updated_at/by` (JPA `AuditAware`) + Envers revision history |
| Approval | cuti & profil — `PENDING → APPROVED/REJECTED` chain |
| IDs | Mostly `Long` auto; `Biodata` keyed by `NIK` (String) |
| Auth impl | Appwrite JWT via `JwtAuthFilter`. Dev no-token → hardcoded admin |
| Config | `application.yml` from env vars. Docker configs in `docker/` |

### Domain Context (Lazy Read)

Start with `CONTEXT-MAP.md`, then pick relevant sub-context:

| If touching... | Read |
|----------------|------|
| `master/` (Profesi, Jabatan, Organisasi) | `docs/context/language-master.md` |
| `pegawai/` terminology | `docs/context/language-pegawai.md` |
| `pegawai/` or `kepegawaian/` rewrite decisions | `docs/context/decisions-pegawai.md` |
| `profil/` (biodata, pendidikan, updateProfile) | `docs/context/language-profil.md` |
| `cuti/` terminology | `docs/context/language-cuti.md` |
| `cuti/` rewrite decisions | `docs/context/decisions-cuti.md` |
| Auth, JWT, Spring profiles | `docs/context/language-security.md` |
| Cross-module dependencies | `docs/context/relationships.md` |
| ADRs | `docs/adr/` |

---

## 5. Environment Variables

Critical env vars (full list: `env.example`):

| Variable | Required | Purpose |
|----------|----------|---------|
| `DB_HOST`, `DB_PORT`, `DB_SCHEMA`, `DB_USER`, `DB_PASSWORD` | ✅ | MariaDB connection |
| `APPWRITE_ENDPOINT`, `APPWRITE_PROJECT_ID`, `APPWRITE_API_KEY` | ✅ | JWT auth provider |
| `KAFKA_BOOTSTRAP_SERVERS` | ✅ | Event bus for gajiBatchRoot |
| `REDIS_HOST`, `REDIS_PORT`, `REDIS_DATABASE` | ✅ | Cache |
| `PENGGAJIAN_URL`, `LAPORAN_KEPEGAWAIAN_URL` | ✅ | External service URLs |
| `SERVER_PORT`, `PROFILE` | ⚠️ | Server config (default: 8080, development) |
| `JENIS_CUTI_TAHUNAN`, `JENIS_CUTI_BESAR`, `JABATAN_MANAGER_SDM`, `JABATAN_SUPERVISOR_SDM` | ⚠️ | Leave & approval config |
| `PROTECTED_DELETE_KTP` | ⚠️ | Flag for protected KTP delete |
| `FLYWAY_ENABLED` | ⚠️ | Toggle Flyway migration (default: false) |
| `REDIS_PASSWORD` | ⚠️ | Redis auth (default: empty) |

---

## 6. Common Tasks & Examples

### Add new CRUD endpoint (e.g. master data)

```
Controller → DTO (request/response) → Repository (JPA/JOOQ) → Service (interface → impl)
```

1. Buat DTO di `dto/{domain}/` — request: `@NotBlank` fields, response: query result mapping
2. Buat Repository di `repositories/{domain}/` — extend `JpaRepository` or JOOQ `DSLContext`
3. Buat Service di `services/{domain}/` — `@Transactional`, return `CustomResult`
4. Buat Controller di `controllers/{domain}/` — extends `AController`, `@GetMapping/@PostMapping/@PutMapping/@DeleteMapping`
5. **WAJIB** `@PreAuthorize("hasRole('ADMIN')")` on mutating methods
6. Unit test: service logic + controller response format

### Run database migration (Flyway)

```bash
# SQL file: src/main/resources/db/migration/V{version}__description.sql
./gradlew flywayMigrate
```

### Generate JOOQ classes after schema change

```bash
./gradlew jooqCodegen
```

### Debug / test query

```bash
./gradlew test --tests "*BiodataDashboardQueryTest*"
```

### Claim & ship an issue

```bash
bd update <id> --claim     # claim
# ... code changes ...
./gradlew build            # WAJIB: zero error sebelum lanjut
bd close <id>               # complete
bd dolt push
git pull --rebase
git push
```

---

## 7. Workflow — Coding Mode Detail

> Flow ini berlaku **hanya saat Coding Mode** (section 2). Grilling Mode punya flow sendiri.

### Skill Wajib

1. **WAJIB aktifkan `/ponytail`** sebelum menulis kode — memaksa solusi paling sederhana, shortest path, YAGNI.
2. **WAJIB gunakan `graphify` & `gitnexus`** untuk eksplorasi kode — **prioritas: `graphify` → `gitnexus` → `grep`**. Grep hanya sebagai last resort.

### Sequence

**Read MD → Read CONTEXT → `/ponytail` → Explore → Write → Test → Build → Update Graph → Update MD → Close → Ship**

| Step | Action |
|------|--------|
| **Read MD** | Baca MD file terkait issue — teliti & pahami claim order |
| **Read CONTEXT** | `docs/context/language-{domain}.md`, ADR, CONTEXT-MAP.md — **jangan tebak-nebak** |
| **Explore** | **Prioritas:** `graphify` (knowledge graph) → `gitnexus` (code intelligence: `query/impact/context`) → `grep` (last resort saja) |
| **Write** | Max 120 lines/file. Split if exceeded. Follow conventions. |
| **Test** | Unit tests **required** for new logic. |
| **Build** | **WAJIB** `./gradlew build` (atau `./gradlew clean compileJava` minimal) — pastikan zero error sebelum lanjut |
| **Update Graph** | `npx gitnexus analyze` (refresh GitNexus) + `/graphify --update` (update knowledge graph via skill) — pastikan graph sesuai perubahan terbaru |
| **Update MD** | Tandai step yang sudah selesai di MD file |
| **Close** | `bd close <id>` — complete issue |
| **Ship** | Commit `<type>: <description>`. `git pull --rebase` → `bd dolt push` → `git push` → verify "up to date". Build & Graph WAJIB up-to-date sebelum step ini. |

### Git mv + Edit (HARD INVARIANT)

1. `git mv old new`
2. `mkdir -p` destination (git mv does NOT create folders)
3. **Read** new path first (Edit tool refuses post-mv until read)
4. Edit header on new path
5. Edit **ALL** importers — no parallel Edit+Add blocks
6. **Single `git add` batch** at the end — all modified files in one command
7. Verify: `git diff --cached` → moved files MUST show content lines, not 0
8. Post-commit: `./gradlew clean compileJava` — if fail, create `fix()` commit (never amend)

---

## 8. Anti-Examples (Do NOT Do)

| Anti-Pattern | Why |
|--------------|-----|
| ❌ Hard-delete rows | Always use `is_deleted` flag. Soft delete only. |
| ❌ `git add` + Edit in parallel | Race: Edit may land after Add snapshot. Batch add at end only. |
| ❌ `git add` per-file between edits | Defeats single-batch guarantee. |
| ❌ Amending broken commits | Policy: **never amend**. Always `fix()` commit. |
| ❌ `compileJava UP-TO-DATE` trust | Gradle content-hash cache can mask missing content. Always `clean compileJava`. |
| ❌ Rename symbols with find-and-replace | Use `gitnexus_rename` — understands call graph. |
| ❌ Resolve out-of-scope errors inline | File new issue instead. |
| ❌ Skip `gitnexus_impact` before edit | Always check blast radius first. |
| ❌ `grep` before `graphify`/`gitnexus` | Prioritas explore: graphify → gitnexus → grep. Jangan grep dulu. |

---

## 9. GitNexus — Code Intelligence

Repo indexed as **kepegawaian** (18385 symbols, 44325 relationships, 300 flows).

```bash
npx gitnexus analyze          # Refresh if index stale
```

### Mandatory

- `gitnexus_impact({target, direction: "upstream"})` **before every edit** → report blast radius to user
- `gitnexus_detect_changes()` **before every commit** → verify affected scope
- Warn user if impact returns HIGH or CRITICAL risk
- `gitnexus_query({query})` for execution flows (better than grep)
- `gitnexus_context({name})` for full callers/callees

### Forbidden

❌ Edit without `gitnexus_impact` first
❌ Ignore HIGH/CRITICAL risk warnings
❌ Find-and-replace rename — use `gitnexus_rename`
❌ Commit without `gitnexus_detect_changes()`

### Resources

- `gitnexus://repo/kepegawaian/context` — overview + freshness check
- `gitnexus://repo/kepegawaian/clusters` — all functional areas
- `gitnexus://repo/kepegawaian/processes` — all execution flows
- `.claude/skills/gitnexus/` — 6 skill files (exploring, impact-analysis, debugging, refactoring, guide, CLI)

### Ignore Files — `.gitnexusignore`

File di root repo (ter-commit). Sintaks gitignore-style: komentar `#`, `!` negasi, trailing `/` untuk dir.

**Semantik** (source: `gitnexus/src/config/ignore-service.ts`):

- `.gitignore` + `.git/info/exclude` **otomatis dihormati**; `.gitnexusignore` dievaluasi **SETELAH** `.gitignore` → **last-match-wins** saat konflik
- `!pattern` bisa membatalkan **hardcoded ignore** GitNexus (mis. `!__tests__/`) — semantik negasi sama seperti `.gitignore`
- `GITNEXUS_NO_GITIGNORE=1` → skip parsing `.gitignore`; `.gitnexusignore` **tetap berlaku**
- GitNexus punya hardcoded default ignore: `.git`, `node_modules`, `dist`, `build`, `out`, `target`, `.idea`, `.vscode`, `.github`, `.husky`, `coverage`, `__tests__` + ekstensi (`.class`, `.jar`, `.pdf`, `.xlsx`, ...) + file tertentu (lockfiles, `.gitignore`, `LICENSE`, `.env*`, ...)
- **Dot-directory sudah di-skip bawaan** oleh glob (`dot:false`) — `.beads/`, `.claude/`, `.agents/`, `.gitnexus/` dll TIDAK akan pernah ter-index meski tanpa ignore file; entri dot-dir di `.gitnexusignore` bersifat defensif + dokumentasi

**Yang di-exclude di project ini:** `graphify-out/` (output graphify), `backup.jsonl` + `.openclaude-profile.json` + `skills-lock.json` (file non-dot yang tadinya lolos), dot-dir agen/tooling (`.agents/`, `.antigravitycli/`, `.beads/`, `.claude/`, `.openclaude/`, `.gitnexus/`, `.gradle/`), IDE/build (`.idea/`, `.vscode/`, `build/`).

> **Temuan (Agustus 2026):** re-index dengan ignore baru hanya menghapus **2 file** (`deleted=2` — `backup.jsonl` + config tool); dir noise dot-dir memang sudah di-skip bawaan sejak awal. Verifikasi: 0 node dari `.beads`/`graphify-out`/`.gitnexus`/`.claude`/`backup.jsonl` di index (cypher `MATCH (n) WHERE n.filePath CONTAINS '...'`).

---

## 10. Issue Tracking

Tool: **`bd`** (beads) + GitHub Issues (`kentoespdam/kepegawaian`).

```bash
bd ready              # Available work
bd show <id>          # Detail
bd update <id> --claim
bd close <id>         # Complete
```

**Rules:**
- `bd` for ALL tracking — no TodoWrite, TaskCreate, markdown TODOs
- Session close: quality gates → `bd dolt push` → `git pull --rebase` → `git push` → verify "up to date"
- Triage labels: `needs-triage`, `needs-info`, `ready-for-agent`, `ready-for-human`, `wont-fix`
- Detail: `docs/agents/issue-tracker.md`, `docs/agents/triage-labels.md`, `docs/agents/domain.md`

---

## 11. Skills

Full catalog: `.claude/skills/`. Key ones:

| Skill | Use Case |
|-------|----------|
| `graphify` | Knowledge graph — eksplorasi/cluster project secara visual (skill di `.agents/skills/graphify/SKILL.md`) |
| `gitnexus-*` | Code intelligence (6 skills — exploring, impact, debugging, refactoring, guide, CLI) |
| `tdd` | Test-first development |
| `diagnose` / `diagnosing-bugs` | Debug / regression |
| `review` | Code review vs standards + spec |
| `grill-me` / `grill-with-docs` | Stress-test plans |
| `domain-modeling` | DDD ubiquitous language |
| `ubiquitous-language` | Extract glossary from conversation |
| `to-prd` / `to-issues` | Convert conversation → PRD → issues |
| `prototype` | Throwaway experimental code |
| `handoff` | Compact session → handoff doc |
| `ponytail` | Force simplest solution (YAGNI) |
| `caveman` | Ultra-compressed mode |

### Ignore Files — `.graphifyignore`

File di root repo (ter-commit). Sintaks **sama seperti `.gitignore`**: komentar `#`, glob, `!` negasi.

**Semantik** (dokumentasi resmi Graphify — bagian *"Ignoring files"*):

- `.gitignore` otomatis dihormati (per-direktori); `.graphifyignore` di-**merge** dan dievaluasi **TERAKHIR** → menang saat konflik (termasuk `!` negasi)
- Hanya bisa **menambah** pengecualian; **TIDAK bisa** re-include file yang sudah di-exclude `.gitignore`
- Scoping subdirektori sama seperti git
- `graphify extract --no-gitignore` → skip `.gitignore` + `.git/info/exclude`; `.graphifyignore` tetap berlaku
- **Dot-directory sudah di-skip bawaan** graphify (`.beads/`, `.claude/`, `.agents/`, `.gitnexus/` dll tidak pernah masuk corpus); `graphify-out/` juga auto-excluded

**Yang di-exclude di project ini:** `graphify-out/`, `backup.jsonl`, `.openclaude-profile.json`, `skills-lock.json` + dot-dir agen/tooling (`.beads/`, `.claude/`, `.agents/`, `.openclaude/`, `.antigravitycli/`, `.gitnexus/`, `.gradle/`, `.idea/`, `.vscode/`, `build/`).

**Update graph:** `graphify update . --force` (wajib `--force` saat node count turun karena pengecualian — tanpa itu graphify menolak overwrite, "fewer nodes"). Full re-extraction (hapus `graphify-out/cache/`) untuk purge file yang sudah keluar dari corpus (fail-closed keep).

> **Temuan (Agustus 2026):** ada **DUA instalasi graphify** — PATH `/home/dev/.local/bin/graphify` (v0.9.35 via uv tool `graphifyy`, yang BENAR dipakai — punya subcommand `update`/`check-update`) vs python dari `.graphify_python` (format manifest LAMA `dict[str,float]`, tidak cocok dengan manifest baru ber-`ast_hash`/`semantic_hash`). Jangan pakai python dari `.graphify_python` untuk update — hasilnya `detect_incremental` salah lapor semua file "new". Selalu `graphify update . --force` via CLI PATH.

> **Temuan (Agustus 2026):** corpus graphify sudah bersih dari dir noise bahkan sebelum `.graphifyignore` dibuat (dot-dir di-skip bawaan). Efek nyata ignore file: mem-purge `backup.jsonl` + 2 file zero-node (`.openclaude-profile.json`, `skills-lock.json`) dan mem-formalkan pengecualian utk portabilitas (`--no-gitignore`).

> **Temuan (Agustus 2026):** 31 file `.sql` (migration V1–V29 + draft) sudah masuk graph setelah `uv tool install 'graphifyy[sql]'` (tree-sitter-sql 0.3.11). Trik re-extract file yang kontennya tidak berubah: kosongkan `ast_hash` mereka di `graphify-out/manifest.json`, lalu `graphify update .` (AST-only rebuild, tanpa LLM). Hasil: 222 node SQL (tabel/kolom, e.g. `biodata`, `biodata_aud`, view `v_pegawai`).

### Graphify semantic enrichment TANPA LLM (kuota Gemini 429)

Pipeline graphify default memanggil LLM (Gemini) untuk **semantic extraction** (docs) dan **community labeling**. Saat kuota habis (429), `graphify . --update` **menolak menimpa** graph lengkap dengan hasil parsial. Solusi yang terbukti (Agustus 2026): **isi semantic cache sendiri** — graphify membaca cache dan melewati LLM untuk file HIT.

**Script reusable: `scripts/graphify-semantic-seed.py`** (murni stdlib, tanpa LLM, tanpa import graphify):

```bash
# 1. Daftar docs yang MISS cache semantic (perlu dianalisis)
python3 scripts/graphify-semantic-seed.py --check

# 2. Baca file tsb → tulis analysis JSON → seed
python3 scripts/graphify-semantic-seed.py --seed docs/adr/0001-xxx.md analysis.json
python3 scripts/graphify-semantic-seed.py --seed <file> <json> --dry-run   # cek hash dulu

# 3. Pastikan 0 miss, lalu jalankan pipeline penuh (semantic 100% cache hit, tanpa Gemini)
python3 scripts/graphify-semantic-seed.py --verify
graphify . --update
GRAPHIFY_VIZ_NODE_LIMIT=20000 graphify cluster-only .   # graph.html + GRAPH_REPORT.md
```

**Mekanik cache yang direplikasi script** (biar entry langsung dibaca graphify):
- Hash entry = `sha256(content + \x00 + salt)`; salt = path relatif lowercase; untuk `.md` content = body **tanpa** YAML frontmatter
- Lokasi: `graphify-out/cache/semantic/pf{fingerprint}/{hash}.json` (subdir `pf*` dipilih otomatis)
- Format: `{nodes, edges, hyperedges}`; node wajib punya `source_file`; id node = `normalize_id(rel_path)` (casefold, non-word → `_`)
- Node konsep boleh id baru (graphify menambahkannya); edge ke node yang sudah ada menyambung graph

**Catatan penting:**
- Semantic cache TIDAK di-commit (git-ignored) — entry yang dianalisis manual bisa diregenerasi dari `scripts/` + analysis JSON, atau backup entry ke repo jika perlu di-commit
- `graphify . --update` (incremental) memakai `semantic_hash`; `graphify update .` memakai `ast_hash` — beda sumber, keduanya di `graphify-out/manifest.json`
- Label komunitas: kalau `.graphify_labels.json` + `.sig` ada, update **reuse + hub-fill** tanpa LLM (hanya komunitas berubah yang di-rename by hub). Untuk nama kaya: `graphify label --backend <ollama|deepseek|openrouter>` (OPENROUTER_API_KEY tersedia di env)

---

## 12. Commit Convention

```
<type>: <description>
```

Types: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`, `style`, `perf`.

**Policy:** never amend. Broken commit → new `fix()` commit.

---

## 13. Pre-Ship Checklist

- [ ] `./gradlew test` — all green
- [ ] `./gradlew build` — clean build
- [ ] `gitnexus_detect_changes()` — scope sesuai
- [ ] `npx gitnexus analyze` — refresh GitNexus index
- [ ] `/graphify --update` — update knowledge graph via skill
- [ ] No out-of-scope errors resolved ad-hoc
- [ ] `bd dolt push` + `git pull --rebase` + `git push` → verify "up to date with origin"

---

## 14. Useful Links (auto-scraped by Freebuff)

- [Spring Boot 4.0 Reference](https://docs.spring.io/spring-boot/4.0/reference/)
- [GitNexus CLI Guide](https://www.npmjs.com/package/gitnexus)
- [Beads Issue Tracker](https://www.npmjs.com/package/beads)
- [Appwrite JWT Auth](https://appwrite.io/docs/products/auth)
- [Flyway DB Migration](https://documentation.red-gate.com/fd/)
- [MariaDB Documentation](https://mariadb.com/kb/en/)
