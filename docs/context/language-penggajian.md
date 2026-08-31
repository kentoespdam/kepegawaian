# Context — Modul Penggajian (Payroll & Batch Pemrosesan Gaji)

Bagian dari [CONTEXT-MAP.md](../../CONTEXT-MAP.md). Baca file ini saat mengerjakan modul `penggajian/`. Keputusan rewrite CQRS/JOOQ modul ini hidup di [`../penggajian-cqrs-claim-order.md`](../penggajian-cqrs-claim-order.md) dan [ADR-0024](../adr/0024-gajibatchroot-kafka-diisolasi-ke-eventpublisher.md).

## Glossary

### Referensi & parameter penggajian (master)

**Dasar Gaji** (DasarGaji):
Tabel acuan gaji pokok yang berlaku pada rentang tanggal tertentu (`tanggalAwal`–`tanggalAkhir`) dengan flag `aktif`. Satu Dasar Gaji berisi banyak **Detail Dasar Gaji**.
_Avoid_: "tarif gaji".

**Detail Dasar Gaji** (DetailDasarGaji):
Satu baris nominal gaji pokok untuk kombinasi **Masa Kerja Golongan** (`mkg`) dan **kode golongan** di bawah satu **Dasar Gaji**. Inilah matriks yang di-lookup saat gaji seorang pegawai dihitung.

**Komponen Gaji** (GajiKomponen):
Satu unsur perhitungan gaji di dalam sebuah **Profil Gaji** — punya `kode`, `nama`, **Jenis Gaji** (pemasukan/potongan), `nilai`, dan `formula` (ekspresi hitung). Urutannya (`urut`) menentukan urutan evaluasi. Endpoint index (`GET /penggajian/komponen/{profilId}/profil`) menerima query param `search` yang di-likeIgnoreCase terhadap `kode` **ATAU** `nama` — satu field untuk pencarian fleksibel tanpa harus tahu apakah input user itu kode atau nama.

**Profil Gaji** (GajiProfil):
Sekumpulan **Komponen Gaji** yang membentuk satu template/skema perhitungan gaji. Menjadi cetakan yang dipakai saat membangun proses gaji per pegawai.

**Tunjangan** (GajiTunjangan):
Nominal tunjangan yang berlaku untuk kombinasi **Jenis Tunjangan** + **Level** + **Golongan**. Di-lookup per pegawai berdasarkan jabatan/golongannya.

**PHDP** (GajiPhdp):
Aturan Penghasilan Dasar Pensiun — daftar baris `kondisi` + `formula` (dengan `urut`) yang dievaluasi untuk menentukan dasar perhitungan terkait pensiun.

**Potongan TKK** (GajiPotonganTkk):
Nominal potongan **TKK** (Tunjangan Kesejahteraan Kerja) untuk kombinasi **Status Pegawai** + **Level** + **Golongan**.

**Parameter Setting** (GajiParameterSetting):
Pasangan `kode`→`nominal` untuk konstanta/parameter global yang dipakai formula penggajian (mis. tarif, batas, faktor).

**Pendapatan Non-Pajak** (GajiPendapatanNonPajak):
Daftar `kode`→`nominal` (+`notes`) komponen penghasilan yang dikecualikan dari perhitungan pajak.

### Batch pemrosesan gaji

**Batch Gaji** (GajiBatchRoot):
Aggregate akar satu putaran penggajian untuk satu **periode**. Membawa **Status Proses**, `totalPegawai`, jejak siapa memproses/menyetujui (nama + jabatan + tanggal), dan `notes`. Anak: satu **Master Gaji** per pegawai. Punya `is_deleted` (soft-delete) dan menerbitkan event Kafka setelah commit.
_Avoid_: "root gaji" sebagai istilah domain — sebut **Batch Gaji**.

**Master Gaji** (GajiBatchMaster):
Satu baris hasil gaji per pegawai di dalam sebuah **Batch Gaji** — menyimpan snapshot identitas pegawai (nipam, nama, jabatan, organisasi, golongan, pangkat) beserta hasil hitungnya. Anak: **Proses Master Gaji**. Hard-delete (tanpa `is_deleted`).

**Proses Master Gaji** (GajiBatchMasterProses):
Baris rincian per **Komponen Gaji** untuk satu **Master Gaji** — `kode`, `urut`, `nama`, **Jenis Gaji**, `nilai`, `formula`, dan `nilaiFormula` (hasil evaluasi). Ini jejak bagaimana satu angka gaji terbentuk.

**Lampiran Batch** (GajiBatchRootLampiran):
Berkas yang dilampirkan pada sebuah **Batch Gaji** (mis. hasil upload **Potongan Tambahan**). Hard-delete.

**Log Error Batch** (GajiBatchRootErrorLogs):
Catatan kegagalan selama pemrosesan sebuah **Batch Gaji**. Hard-delete.

**Potongan TKK Batch** (GajiBatchPotonganTkk):
Snapshot nilai **Potongan TKK** yang di-upload/diterapkan pada sebuah batch. Hard-delete.

### Enum & konsep alur

**Status Proses** (EProsesGaji):
State-machine sebuah **Batch Gaji**, berlabel Indonesia: PENDING → PROSES ("Proses Sedang Berjalan") → **Verifikasi Tahap 1** (WAIT_VERIFICATION_PHASE_1) → **Verifikasi Tahap 2** (WAIT_VERIFICATION_PHASE_2) → **Menunggu Approval** (WAIT_APPROVAL) → FINISHED ("Selesai"); FAILED ("Gagal") bila gagal. Transisi digerakkan verb workflow `reprocess`/`verify1`/`verify2`/`accept`.

**Jenis Gaji** (EJenisGaji):
Arah sebuah komponen terhadap gaji: **Pemasukan** (PEMASUKAN — menambah) atau **Potongan** (POTONGAN — mengurangi); NONE ("-") untuk tak terklasifikasi.

**Jenis Potongan** (EJenisPotonganGaji):
Membedakan **Potongan TKK** (POTONGAN_TKK) dari **Potongan Tambahan** (POTONGAN_TAMBAHAN — potongan ad-hoc yang di-upload terpisah).

**Potongan Tambahan** (Additional / prefix `ADD_`):
Potongan ad-hoc yang di-upload ke sebuah **Batch Gaji** setelah proses awal. Baris **Proses Master Gaji** hasil upload ini diberi kode berawalan `ADD_`. Menerapkannya memicu **recalculate** ulang total; membatalkannya = **rollback** (hapus semua baris `ADD_%` lalu nol-kan total).
