# JOOQ codegen dijalankan lewat `GenerationTool` di satu task imperatif, bukan plugin official

ADR-0004 mengunci *rantai* codegen (Gradle nyalakan Testcontainers MariaDB → Flyway migrate → JOOQ codegen → kontainer mati) tapi tidak menentukan **bagaimana build menggerakkan generator**. ADR ini menutup fork itu.

Gesekannya: Testcontainers memberi **JDBC URL acak** tiap build (host/port random), sedangkan plugin official `org.jooq.jooq-codegen-gradle` (direkomendasikan jOOQ sejak 3.19) membaca blok `jooq { configuration { jdbc { url } } }` yang **statis**. Menyatukan keduanya menuntut URL runtime-resolved disuntik ke task yang confignya didesain statis.

## Keputusan

Codegen **tidak** memakai plugin `org.jooq.jooq-codegen-gradle`. Sebagai gantinya: **satu custom Gradle task imperatif** memanggil `org.jooq.codegen.GenerationTool.generate(config)` langsung. Task itu sendiri yang memiliki seluruh rantai ADR-0004:

1. start kontainer Testcontainers MariaDB,
2. jalankan Flyway migrate terhadapnya,
3. ambil **JDBC URL live** kontainer sebagai variabel lokal dan serahkan ke `org.jooq.meta.jaxb.Jdbc` di dalam `Configuration`,
4. `GenerationTool.generate(config)`,
5. stop kontainer.

Dependency codegen (`org.jooq:jooq-codegen` + driver MariaDB + Flyway + Testcontainers) diisolasi di konfigurasi `jooqCodegen` agar tidak bocor ke classpath runtime aplikasi.

## Considered Options

- **Plugin official `org.jooq.jooq-codegen-gradle` + bridging URL dinamis** (ditolak): task kustom nyalakan kontainer + Flyway, lalu URL-nya disuntik ke task `jooqCodegen` lewat lazy provider / `afterEvaluate`, dengan task-ordering supaya kontainer hidup sebelum codegen dan mati sesudahnya. Jalur yang diberkati jOOQ dan confignya deklaratif — tapi config plugin didesain **statis**; menyuntik URL random butuh senam lazy-provider + mengoreografikan lifecycle kontainer **di sekitar** task yang bukan milik kita. Lebih banyak bagian bergerak demi keuntungan deklaratif yang justru digugurkan oleh URL random.
- **`GenerationTool.generate()` di satu task imperatif** (dipilih): rantai ADR-0004 memang imperatif & stateful (handle kontainer hidup dijalin lewat tiga langkah). URL random cuma jadi variabel lokal — nol bridging. Kontrol penuh up→migrate→generate→down di satu tempat, KISS. Harga: merangkai API `GenerationTool` (kecil, stabil) sendiri alih-alih satu baris `plugins {}`, dan melepas kenyamanan deklaratif plugin.

## Consequences

- Build script (`build.gradle.kts`) mendaftarkan task codegen kustom. Task ini **tidak** disambungkan ke `compileJava` — ia dijalankan **manual** hanya saat skema berubah, dan output-nya di-commit ke git (lihat ADR-0015). Akibatnya `compileJava` (dan CI) tidak pernah menyalakan kontainer.
- API `GenerationTool` / `org.jooq.meta.jaxb.*` (`Configuration`, `Jdbc`, `Generator`, `Database`, `Target`) stabil lintas rilis jOOQ; risiko maintenance rendah meski tak memakai plugin.
- Karena task memiliki lifecycle kontainer secara eksplisit, reuse kontainer saat dev lokal (untuk meringankan ADR-0004) diatur di task yang sama, bukan via konfigurasi plugin.
- Konsisten dengan ADR-0006 (layer-first) & nilai KISS proyek: satu titik kendali, tanpa lapisan abstraksi plugin.
