# Project Rules for AI Agents: Token Conservation

- **Root Agent (Strict Manager)**:
  - Root agent bertindak HANYA sebagai Manager/Orchestrator dan High-level Planner.
  - DILARANG melakukan manipulasi file, riset, atau eksekusi teknis langsung.
  - WAJIB mendelegasikan tugas ke subagent (model `flash_lite`) untuk semua pekerjaan teknis.
  - Hanya menerima ringkasan laporan dari subagent.

- **Sub-Agent Reporting Protocol (STRICT)**:
  - Sub-agent HANYA boleh melaporkan **fakta/hasil mentah** dari tugas yang diberikan (contoh: hasil analisis, output command, daftar temuan).
  - Sub-agent **DILARANG KERAS**:
    - Memberikan saran, rekomendasi, atau opini tentang langkah berikutnya.
    - Mengambil keputusan di luar lingkup tugas yang diberikan.
    - Menyarankan pendekatan alternatif kecuali diminta root agent.
    - Memulai pekerjaan tambahan yang tidak diminta secara eksplisit.
  - Sub-agent harus menutup laporannya dengan: **"Laporan selesai. Menunggu instruksi berikutnya dari root agent."**
  - Root agent **WAJIB** mengevaluasi laporan secara mandiri dan menentukan langkah selanjutnya — tidak boleh langsung menyetujui saran dari sub-agent.

- **Sub-Agent Error Handling**:
  - Jika sub-agent menemukan error, WAJIB melaporkan detail error ke root agent. DILARANG melakukan *self-fix loop*.
  - Root agent menentukan strategi perbaikan dan mendelegasikan kembali.
  - Jika dalam 3x percobaan perbaikan error tetap terjadi, root agent WAJIB memerintahkan riset ke internet untuk best practice dan referensi source code terbaru terkait library yang digunakan.

- **Optimasi Context Caching & Prefix Stability**:
  - **Static Prefix**: Gunakan template prefix standar untuk setiap instruksi subagent.
  - **Dynamic Suffix**: Letakkan parameter variabel (file, target) di akhir prompt (suffix).
  - **Lean Payload**: Hindari meneruskan riwayat percakapan; berikan payload instruksi yang relevan saja.

- **Token & Efficiency**:
  - **Concise Communication**: Langsung ke poin, tanpa basa-basi.
  - **Targeted Modifications**: Gunakan `replace_file_content` (micro-diffs).
  - **Selective Reading**: Hanya baca bagian file yang relevan.
  - **Quiet Commands**: Gunakan flag peringkas output terminal (`-q`, `head`, `grep`).

- **Mandatory Pre-Coding Gates**:
  - **Ponytail**: WAJIB load skill `/ponytail` sebelum modifikasi kode.
  - **Issue Tracking (Beads/bd)**: Klaim tugas (`bd update <id> --claim`), tutup tugas (`bd close <id>`). DILARANG membuat todo list manual.
  - **GitNexus First**: WAJIB `gitnexus_impact` & `gitnexus_query` sebelum eksplorasi atau modifikasi.
  - **Graphify**: WAJIB gunakan `graphify query "<query>"` atau `graphify path "<A>" "<B>"` untuk query arsitektur/konsep, jalankan `graphify . --update` jika ada perubahan modul/struktur. Output graph berada di `graphify-out/` (`graph.html`, `graph.json`, `GRAPH_REPORT.md`). Untuk semantic extraction, TIDAK PERLU menggunakan external LLM model secara langsung/independen, melainkan proses ekstraksi semantik dilakukan sendiri melalui sub-agent internal (`flash_lite`).
  - **Sandbox Policy**: WAJIB gunakan `BypassSandbox: true` untuk semua perintah `git` dan `bd`.

- **File Size & Modularity (Token Conservation)**:
  - Target: 150 – 250 LOC per file.
  - Hard Ceiling: Max 300 LOC. WAJIB modularisasi jika mendekati batas.

- **Java & Gradle Toolchain**:
  - Java 25 & Spring Boot 4.0.3 (GraalVM JDK 25).
  - WAJIB gunakan Gradle Wrapper:
    - Build: `./gradlew build`
    - Run Dev: `./gradlew bootRun`
    - Test: `./gradlew test`
    - Fat JAR: `./gradlew bootJar`

- **Core Architecture & Conventions**:
  - **CQRS-lite**:
    - Query: `*QueryService` (`@Transactional(readOnly = true)`) → `*QueryRepository` (jOOQ `DSLContext` → Java record DTOs).
    - Command: `*CommandService` (`@Transactional`) → `*Repository` (Spring Data JPA → `@Entity` + Envers).
  - **DTO & Response**: Selalu kembalikan DTO / record via `CustomResult` (`any`, `page`, dll). DILARANG mengembalikan `@Entity` dari Controller.
  - **Spring Boot 4 / Java 25 Standards**:
    - Jakarta EE 11 (`jakarta.*`, zero `javax.*`).
    - Jackson 3 (`tools.jackson`).
    - Test: JUnit 5, `@MockitoBean` (bukan `@MockBean`), `@ServiceConnection` (Testcontainers), ArchUnit (`1.4.1`).
  - **Database & Migration**:
    - MariaDB + Flyway (`V<YYYYMMDDHHmmss>__<desc>.sql`).
    - Redis: Serializer JSON, mandatory TTL, key prefix `kepegawaian:<domain>:<id>`.

- **Session Completion & Checklist**:
  - Jalankan `gitnexus_detect_changes()` dan `./gradlew test`.
  - WAJIB update `CHECKLIST.md` dan push: `git pull --rebase` && `git push` (WAJIB gunakan `BypassSandbox: true`).
  - Rujuk `CODING_RULES.md` & `CHECKLIST.md` untuk standar detail.
