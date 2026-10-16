# Project Rules for AI Agents: Token Conservation

- **Root Agent (Strict Manager)**:
  - Root agent bertindak HANYA sebagai Manager/Orchestrator dan High-level Planner.
  - DILARANG melakukan manipulasi file, riset, atau eksekusi teknis langsung.
  - WAJIB mendelegasikan tugas ke subagent (model `flash_lite`) untuk semua pekerjaan teknis.
  - Hanya menerima ringkasan laporan dari subagent.

- **Sub-Agent Reporting Protocol (STRICT)**:
  - Sub-agent WAJIB melaporkan ringkasan padat/eksekutif berbasis fakta (high-signal, zero fluff) dari hasil analisis/tugas tanpa menghilangkan konteks teknis esensial, serta DILARANG melakukan dump raw text/output panjang yang memboroskan token di root agent.
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
  - **GitNexus First**: WAJIB jalankan `gitnexus_impact` & `gitnexus_query` sebelum eksplorasi atau modifikasi kode.
  - **Graphify**: WAJIB gunakan `graphify query` / `path` untuk audit arsitektur; jalankan `graphify . --update` jika ada perubahan modul/struktur. Ekstraksi semantik via sub-agent internal (`flash_lite`).
  - **Sandbox Policy**: WAJIB gunakan `BypassSandbox: true` untuk semua perintah `git` dan `bd`.

- **Code Intelligence Optimization (Graphify & GitNexus)**:
  - **Sinergi Analisa**: Gunakan **Graphify** untuk arsitektur makro (knowledge graph, clustering komunitas, batasan domain/modul) dan **GitNexus** untuk navigasi mikro (AST, call graph, execution flows, blast radius).
  - **Sesi Grilling (Planning & Stress-Testing Desain)**:
    - **Graphify**: Jalankan `graphify query "<konsep>"` atau `graphify path "<A>" "<B>"` untuk memvalidasi batas modul dan dependensi arsitektur sebelum menyusun opsi desain atau bertanya ke user.
    - **GitNexus**: Gunakan `query({search_query: "<alur/konsep>"})` dan `context({name: "<simbol>"})` untuk menelusuri trace execution flow konkret. Pertanyaan grilling WAJIB grounded pada kode aktual untuk memverifikasi asumsi implementasi tanpa menebak.
  - **Sesi Coding (Implementasi & Refactoring)**:
    - **GitNexus (Pre-Edit)**: WAJIB `impact({target: "<simbol>", direction: "upstream"})` sebelum mengubah metode/kelas. Laporkan blast radius (direct callers, affected processes). Stop & konfirmasi jika risiko HIGH/CRITICAL.
    - **GitNexus (Refactoring & Pre-Commit)**: Gunakan `rename` berbasis call graph (DILARANG find-and-replace). WAJIB jalankan `detect_changes()` sebelum commit untuk memvalidasi alur yang terdampak.
    - **Graphify**: Pastikan desain tetap modular (target 150–250 LOC). Jalankan `graphify . --update` saat menambah/mengubah struktur paket atau modul.
  - **Sesi Diagnosa Bug (Debugging & Root Cause Analysis)**:
    - **Graphify**: Gunakan `graphify path "<KomponenA>" "<KomponenB>"` untuk menelusuri rantai dependensi antar-layer/modul saat menganalisis bug sistemik atau anomali alur data lintas modul.
    - **GitNexus (Tracing)**: Telusuri execution flow bug via `gitnexus://repo/kepegawaian/process/{name}` atau `query({search_query: "<error/flow>"})` dari controller/entry point hingga titik kegagalan.
    - **GitNexus (Callers & Taint)**: Gunakan `context({name: "<method>"})` untuk inspeksi caller/callee dan `explain({target: "<fileOrSymbol>"})` untuk analisis aliran data (source-to-sink).
    - **GitNexus (Fix Safety)**: WAJIB jalankan `impact` pada fungsi target perbaikan sebelum menulis fix untuk mencegah side-effect atau regresi baru.

- **File Size & Modularity (Token Conservation)**:
  - Target: 150 – 250 LOC per file.
  - Hard Ceiling: Max 300 LOC. WAJIB modularisasi jika mendekati batas.

- **Java & Gradle Toolchain**:
  - Java 25 & Spring Boot 4.0.3 (GraalVM JDK 25).
  - WAJIB gunakan Gradle Wrapper:
    - Build: `./gradlew build`
    - Run Dev: `./gradlew bootRun`
    - Test: `./gradlew test` (WAJIB menggunakan hanya 1 sub-agent dan DILARANG KERAS paralel dengan agent lain saat menjalankan test atau pengujian untuk mencegah lag sistem)
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
