# Context — Modul Laporan Kepegawaian

Bagian dari [CONTEXT-MAP.md](../../CONTEXT-MAP.md). Baca file ini saat mengerjakan fitur pelaporan kepegawaian (`/laporan/kepegawaian/*`).

## Glossary Domain Laporan

**DUK (Daftar Urut Kepangkatan)**:
Daftar susunan urutan pegawai negeri/karyawan yang disusun berdasarkan peringkat kepangkatan/golongan ruang, TMT golongan, jabatan, TMT jabatan, masa kerja (MK), dan pendidikan. Berfungsi sebagai instrumen pembinaan karier, evaluasi promosi, dan penataan hierarki pegawai.

**DNP (Daftar Nominatif Pegawai)**:
Daftar rekapitulasi data administratif seluruh pegawai aktif yang dikelompokkan berdasarkan unit organisasi/kerja, level jabatan, pangkat/golongan, dan masa kerja (MK & MKG). Digunakan untuk audit formasi dan daftar riwayat ringkas kepegawaian.

**SO (Susunan Organisasi / Struktur Organisasi)**:
Laporan dan bagan pohon hierarki organisasi kepegawaian yang memetakan kedudukan struktural **Jabatan** (sebagai entitas utama, secara default mencakup seluruh tingkatan dari Dewan Pengawas dan Direksi hingga Staf / level 7), dilengkapi dengan pejabat aktif (**Pegawai**) yang menempatinya melalui LEFT JOIN, NIPAM, dan unit kerja **Organisasi**.
- Posisi jabatan yang belum terisi pejabat aktif (lowong) tetap muncul dalam struktur organisasi dan ditandai dengan flag `isLowong = true` serta `namaPegawai = null`.
- Dikonsumsi oleh komponen bagan visualisasi frontend (seperti `d3-org-chart`) dalam format **Flat Array** (relasi `id` dan `parentId`), dengan opsi parameter `?format=tree` untuk format bersarang (`children`), serta filter opsional `?maxLevel=N` jika ingin membatasi kedalaman struktur.

**LTA (Lepas Tanggungan Anak)**:
Laporan pemantauan anak pegawai yang mendekati batas usia tunjangan atau batas akhir kriteria tanggungan (usia maksimal, status pendidikan/kuliah, pernikahan). Filter evaluasi mencakup:
- `BULAN_INI`: Anak yang lepas hak tunjangannya pada bulan berjalan.
- `GTE_1`: Monitoring 1 bulan ke depan.
- `GTE_2`: Monitoring 2 bulan ke depan.

**Kenaikan Berkala**:
Laporan pemantauan pegawai yang jatuh tempo Kenaikan Gaji Berkala (KGB) atau Kenaikan Pangkat Berkala berdasarkan TMT SK terakhir dan masa kerja reguler.

**Laporan Kontrak**:
Monitoring pegawai berstatus Perjanjian Kerja Waktu Tertentu (PKWT)/kontrak yang memuat sisa durasi kontrak, tanggal mulai/akhir, serta status evaluasi perpanjangan kontrak.

**Laporan Mutasi**:
Rekapitulasi riwayat mutasi dan rotasi pegawai (perubahan unit organisasi, jabatan, atau profesi) berdasarkan penerbitan Surat Keputusan (SK Mutasi) dalam periode tertentu.

**Statistik Kepegawaian**:
Laporan agregasi dan distribusi demografis seluruh pegawai (komposisi jenis kelamin, rentang usia, tingkat pendidikan formal, agama, golongan pangkat, dan status pegawai).

---

## Arsitektur Asynchronous Claim Order Pattern

Untuk proses ekspor berkas Excel (.xlsx), sistem menggunakan pola **Asynchronous Claim Order** menggantikan synchronous streaming (`ByteArrayResource`):

```
[ Client ] ──────── 1. POST /excel (Filter) ────────► [ Controller ]
           ◄─── 2. Return Claim Ticket (202) ────────
                                                            │ 3. Dispatch Async Job
                                                            ▼
[ File Storage ] ◄─── 5. Simpan File (.xlsx) ─────── [ Async Worker / Service ]
 (FileUploadUtil)                                      (JOOQ + Apache POI)
                                                            │ 6. Update Status (READY)
                                                            ▼
[ Client ] ──────── 7. GET /claim/{ticketId} ───────► [ Claim Endpoint ]
           ◄─── 8. Stream File Download ─────────────
```

### Tahapan Alur Kerja:

1. **Order Placement (Request)**:
   Klien mengirimkan permintaan ekspor dengan kriteria filter tertentu. Controller memvalidasi request, mendaftarkan job antrean/tiket klaim, dan segera mengembalikan respons HTTP 202 Accepted berisi `ticketId` / `orderId`.

2. **Asynchronous Generation**:
   Worker mengeksekusi query database menggunakan JOOQ DSL, memuat template Excel terkait dari classpath, dan mengisi baris data melalui Apache POI di thread latar belakang.

3. **Temporary Storage (`FileUploadUtilImpl`)**:
   Berkas Excel yang selesai dirangkai disimpan sementara di Local File System melalui `FileUploadUtilImpl` dengan identifikasi berkas acak/terhash. Metadata tiket diperbarui ke status `READY` beserta referensi lokasi berkas.

4. **Claim Retrieval (Download)**:
   Klien memeriksa status tiket atau menukarkan `ticketId` ke endpoint klaim berkas. Berkas di-stream ke klien sebagai file attachment dan status tiket ditandai sebagai telah diambil.

5. **Retention Housekeeping (`@Scheduled`)**:
   Berkas di Local File System bersifat sementara (temporary). Spring Boot `@Scheduled` berjalan terjadwal secara periodik untuk membersihkan dan menghapus berkas-berkas laporan yang telah melewati batas kedaluwarsa waktu simpan (TTL, mis. > 24 jam).

---

## Aturan Keamanan & Otorisasi

- **Re-use Permission Modul**: Tidak diperkenankan membuat role/authority redundan baru khusus laporan jika sudah ada authority modul operasional.
- Akses data laporan dan ekspor menggunakan permission modul terkait, contoh:
  - Laporan DUK, DNP, SO, Statistik: `hasAuthority('PEGAWAI:READ')`
  - Laporan Mutasi: permission SK / Mutasi terkait
  - Laporan Cuti: permission modul Cuti
- Setiap klaim tiket unduhan divalidasi kepemilikan atau hak aksesnya sesuai otorisasi pemohon awal.
