# Python 3 CLI untuk Tooling Migrasi Data Legacy

Standalone microapp migrasi data dibangun menggunakan **Python 3 CLI** (berlokasi di `tools/migration/`), bukan menggunakan Java atau skrip SQL prosedural.

## Konteks

Repositori utama `kepegawaian` menggunakan Java 25 dan Spring Boot 4. Namun, microapp migrasi data legacy merupakan perkakas transisi operasional yang dieksekusi berkali-kali selama masa pengembangan untuk memvalidasi dan membersihkan anomali data sebelum eksekusi tunggal (*cutover*) di staging/production. Tool ini membutuhkan siklus iterasi yang cepat, pembersihan string/format yang fleksibel, dan beban eksekusi yang ringan.

## Considered Options

- **Standalone Java CLI** (ditolak): Memerlukan kompilasi ulang Gradle di setiap penyesuaian aturan mapping data, membuat siklus debug data kotor di development menjadi lambat.
- **Node.js / TypeScript CLI** (ditolak): Membutuhkan setup runtime Node dan dependensi `node_modules` tambahan tanpa memberikan keuntungan signifikan dibanding Python.
- **Python 3 CLI** (dipilih): Sangat ringan, tidak membutuhkan kompilasi, memiliki pustaka manipulasi data dan konektor MariaDB yang matang, serta memungkinkan eksekusi modular dengan flag CLI yang fleksibel (`--domain`, `--dry-run`, `--limit`).

## Consequences

- Tool migrasi hidup di direktori mandiri `tools/migration/` dengan `requirements.txt` atau `pyproject.toml` sendiri.
- Skema tabel target dan nilai enum Java dimodelkan secara terisolasi di dalam modul Python (menggunakan konstanta/dataclass) tanpa menyentuh kode Java.
- Tim dapat menjalankan proses migrasi secara instan dari terminal Linux: `python3 -m migration --domain=pegawai --dry-run`.
