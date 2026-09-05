# Standalone Migration Microapp Terpisah dari Aplikasi Utama

Microapp migrasi data dibangun sebagai aplikasi mandiri yang **terpisah sepenuhnya (decoupled)** dari aplikasi utama `kepegawaian`, bukan sebagai modul atau profile yang tertanam di dalam Spring Boot API.

## Konteks

Migrasi data dari database monolitik legacy `smartoffice` ke `kepegawaian_dev_new` pada hakikatnya adalah proses transisi sekali pakai (*one-time cutover*) di lingkungan staging/production. Jika tool migrasi dimasukkan ke dalam aplikasi utama, terdapat risiko keterikatan dependensi, potensi eksekusi tidak sengaja di production, dan penambahan beban artefak (bloat) pada service yang seharusnya murni melayani REST API.

## Considered Options

- **Embedded Spring Boot Runner / Profile** (ditolak): Menambah ukuran jar, mengekspos konfigurasi koneksi database legacy ke environment production, dan menambah risiko human-error eksekusi runner pada aplikasi inti.
- **Standalone Migration Microapp** (dipilih): Aplikasi CLI mandiri di folder/subdirektori terpisah (misalnya `migration-app/`), memiliki dependensi sendiri, mengeksekusi proses ETL batch secara terisolasi, dan aman diuji coba berkali-kali di dev sebelum eksekusi 1x saat cutover production.

## Consequences

- Aplikasi utama `kepegawaian` tetap bersih, ramping, dan bebas dari dependensi atau konfigurasi database legacy.
- Runner harus merefleksikan skema dan aturan integritas target (termasuk penanganan tabel audit Envers dan enum) secara eksplisit.
- Runner dirancang idempotent dan re-entrant: aman dijalankan berulang kali di dev tanpa duplikasi data, namun siap untuk eksekusi tunggal (*one-shot cutover*) di staging/production.
