# ~~Flyway upgrade & config fix~~ ✅

Semua item selesai:

1. ✅ Flyway Gradle plugin + artifact versi `12.8.1` seragam via `flywayVersion`.
2. ✅ Konfigurasi `locations`, `driver`, `url` sesuai ekspektasi Flyway terbaru.
3. ✅ `./gradlew flywayInfo` — BUILD SUCCESSFUL. 36 migration sukses, schema versi 5.1.0, tanpa pending migration. (Minor warning `WSREP_ON` dari MariaDB — non-fatal.)