# Kepegawaian

Sistem manajemen kepegawaian (HRM) untuk PERUMDAMTS. Migrasi dari aplikasi SmartOffice legacy, dengan perubahan penamaan tabel/kolom dari bahasa Inggris ke bahasa Indonesia, penambahan tabel baru, dan penambahan kolom audit. Data dari SmartOffice sudah dimigrasikan sepenuhnya.

## Language

**Pegawai**:
Individu yang terdaftar dalam sistem kepegawaian, diidentifikasi secara unik oleh NIPAM.
_Avoid_: Employee, staff, worker

**NIPAM**:
Nomor Induk Pegawai Air Minum — identifier unik pegawai dalam sistem.
_Avoid_: Employee ID, staff number

**Biodata**:
Data pribadi seseorang yang diidentifikasi oleh NIK. Satu Biodata bisa berelasi dengan satu Pegawai.
_Avoid_: Profile, personal data

**SmartOffice**:
Aplikasi legacy yang menjadi sumber data migrasi. Database asli di `192.168.230.84:3307/smartoffice`. Data sudah selesai dimigrasikan.
_Avoid_: Old system, legacy app

**Riwayat SK**:
Catatan historis Surat Keputusan yang diterbitkan untuk seorang Pegawai.
_Avoid_: SK history, decree record

**Mutasi**:
Perpindahan Pegawai antar jabatan, organisasi, atau golongan.
_Avoid_: Transfer, reassignment

**Command Service**:
Service yang menangani operasi tulis (create, update, soft-delete) menggunakan JPA/Hibernate.
_Avoid_: Write service

**Query Service**:
Service yang menangani operasi baca (list, detail, search) menggunakan JOOQ.
_Avoid_: Read service

**Simple Audit**:
Kolom `created_at`, `created_by`, `updated_at`, `updated_by` yang otomatis diisi via JPA AuditAware. Berlaku untuk semua entity.
_Avoid_: Basic audit, field tracking

**Full Revision History**:
Envers `@Audited` yang mencatat setiap versi historis data lengkap di tabel `_aud`. Hanya berlaku untuk entity Tier 1 (Pegawai, Riwayat SK/SP/Mutasi/Kontrak/Terminasi, CutiPegawai, GajiProfil).
_Avoid_: Audit trail (terlalu ambigu — bisa berarti simple audit atau full history)

**Profile Update**:
Mekanisme approval saat Pegawai mengubah data profil sendiri. Menggunakan entity `ProfileUpdate`/`PegawaiProfilUpdate` terpisah, bukan flag pada entity utama.
_Avoid_: changedStatus

## Relationships

- Satu **Pegawai** memiliki tepat satu **Biodata** (via NIK)
- Satu **Pegawai** memiliki satu **Jabatan**, satu **Organisasi**, satu **Golongan**, satu **Grade**
- Satu **Pegawai** memiliki banyak **Riwayat SK** dan **Mutasi**
- Setiap domain memiliki **Command Service** (JPA) dan **Query Service** (JOOQ)
- Setiap domain memiliki `repositories/{domain}/jpa/` dan `repositories/{domain}/jooq/`

## Flagged ambiguities

- "Audit" — digunakan untuk dua hal berbeda. Diselesaikan: **Simple Audit** (kolom tracking) vs **Full Revision History** (Envers). Lihat definisi di atas.
