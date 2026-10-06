# Pemilihan OpenPDF dan Desain Endpoint Slip Gaji Pegawai

Kebutuhan pencetakan Slip Gaji pegawai dalam format PDF (`GET /penggajian/batch/master/{id}/slip-gaji`) memerlukan library PDF engine yang kompatibel dengan Java 25 dan Spring Boot 4. Kami memutuskan menggunakan **OpenPDF** (`com.github.librepdf:openpdf`), memberlakukan guard ketat status batch `FINISHED` serta otorisasi kepemilikan data / role HRD-Admin, dan mengonfigurasi kop surat secara dinamis di `application.yaml`.

## Considered Options
- **OpenPDF**: Open-source (LGPL/MPL), ringan, cepat, tanpa dependensi browser headless, dan stabil pada Java 25 runtime. (Dipilih)
- **iText 7/8**: Memiliki lisensi AGPL (copyleft) yang berisiko bagi codebase enterprise atau memerlukan lisensi berbayar komersial.
- **JasperReports**: Berat, overhead kompilasi jrxml, dan kompleksitas kompatibilitas bytecode Java 25.
- **OpenHTMLtoPDF / Thymeleaf**: Membutuhkan dependency rendering HTML-to-PDF ekstra yang menambah footprint memory.

## Consequences
- Dependensi `com.github.librepdf:openpdf` ditambahkan ke `build.gradle.kts`.
- Desain PDF slip gaji dibangun secara programmatic menggunakan tabel dan elemen OpenPDF sesuai spesifikasi layout PDAM Tirta Satria Banyumas.
- Guard otorisasi memastikan pegawai hanya dapat melihat slip miliknya sendiri atau diakses oleh admin/HRD, dan hanya ketika batch gaji berstatus `FINISHED`.
- Konfigurasi kop surat dan logo PDAM Tirta Satria disiapkan pada `application.yaml` dengan nilai default yang dapat di-override.
