# Gaji KPI: Batch Upsert, Hard Delete, dan Tanpa Envers

Entitas `GajiKpi` (tunjangan kinerja bulanan dan PPh 21 TER per pegawai) awalnya dibuat dengan audit Hibernate Envers (`gaji_kpi_aud`) dan soft-delete (`is_deleted`), yang menuntut logika carcass revival yang kompleks (ADR-0005). Seiring dengan diperkenalkannya fitur batch upload via Excel (menggunakan Apache Fesod) dan batch upsert level database (`ON DUPLICATE KEY UPDATE` via jOOQ), soft-delete dan Envers menimbulkan overhead basis data yang besar, kompleksitas alur, serta potensi benturan kunci duplikat (*duplicate-key collisions*).

Kami memutuskan untuk:
1. **Menghapus Hibernate Envers (`@Audited`) dan drop tabel `gaji_kpi_aud`** guna meniadakan overhead pembuatan revisi audit pada operasi tulis massal bulanan.
2. **Menghapus soft-delete (`is_deleted`) dan beralih ke direct hard delete**, konsisten dengan `GajiBatchMaster` dan record pemrosesan penggajian lainnya. Constraint `UNIQUE(nipam, periode)` ditegakkan langsung tanpa penanganan record bangkai (*carcass*).
3. **Mendukung batch upload Excel (`.xlsx` dan `.xls`)** menggunakan Apache Fesod dengan validasi *all-or-nothing* (verifikasi NIPAM terhadap master pegawai aktif dan kesesuaian periode baris) serta eksekusi batch `ON DUPLICATE KEY UPDATE` melalui jOOQ.
4. **Menyediakan endpoint unduh template Excel statis** (`GET /penggajian/kpi/template/download`) yang sesuai dengan standar format impor.

## Considered Options

- **Mempertahankan Envers dan Soft-Delete dengan Revive-on-Create (ADR-0005)** (ditolak): Memerlukan lookup record terhapus untuk setiap baris impor dan menghasilkan record revisi Envers massal. Ini memicu N+1 round-trip, overhead penyimpanan pada histori audit yang tidak esensial untuk data input eksternal bulanan, dan rentan terhadap tabrakan kunci unik.
- **Batch Upsert Native jOOQ + Hard Delete Tanpa Envers** (dipilih): Memperlakukan `GajiKpi` sebagai data transaksi periodik eksternal. DB menangani upsert langsung secara atomik via `ON DUPLICATE KEY UPDATE`, penghapusan langsung menghapus data fisik (hard delete), dan integritas data diverifikasi di muka sebelum batch dieksekusi.

## Consequences

- **Performa Batch Optimal**: Impor ratusan data KPI per periode berjalan cepat dalam satu batch transaction tanpa overhead penulisan baris revisi Envers.
- **Eliminasi Kompleksitas Bangkai**: Tidak ada lagi query native untuk mendeteksi bangkai terhapus maupun manipulasi state `is_deleted` di level repository dan command service.
- **Validasi All-or-Nothing**: Seluruh baris dalam berkas Excel divalidasi sebelum disimpan; kegagalan satu baris (misal NIPAM tidak valid atau periode mismatch) membatalkan seluruh proses simpan.
- **Kemudahan Integrasi User**: Tersedia endpoint pengunduhan template Excel statis sehingga format data yang di-upload selalu selaras dengan skema parser.
