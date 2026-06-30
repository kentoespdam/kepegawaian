# Context — Modul Cuti (Pengajuan & Approval Cuti)

Bagian dari [CONTEXT-MAP.md](../../CONTEXT-MAP.md). Baca file ini saat mengerjakan modul `cuti/`. Untuk keputusan rewrite, baca juga [`decisions-cuti.md`](./decisions-cuti.md).

## Glossary

**Cuti**:
Hak pegawai untuk tidak masuk kerja dalam periode tertentu, diajukan dan disetujui berjenjang. Satu permohonan = satu **Cuti Pegawai**.

**Cuti Pegawai** (CutiPegawai):
Aggregate akar modul ini — satu baris permohonan cuti milik seorang pegawai. Membawa rentang tanggal (mulai/selesai), **Jenis Cuti**, jumlah hari + hari kerja, status approval, level approval berjalan, **PIC Saat Ini** (jabatan yang giliran memproses), dan snapshot kuota awal/akhir. Anak murni: **Approval**, **Approval Chain**, **Klaim Detail**.

**Jenis Cuti** (CutiJenis):
Tipe cuti — pohon berinduk (mis. Cuti Tahunan, Cuti Besar, Cuti Ibadah, Cuti Sakit, Cuti Melahirkan). Punya `maxHari` dan flag `potongKuotaTahunan` (apakah memotong **Kuota Cuti** tahunan). Lifecycle sendiri (di-CRUD admin).
_Avoid_: "kategori cuti".

**Kuota Cuti** (CutiKuota):
Jatah cuti per-pegawai per-**tahun**: `kuota`, `kuotaTerpakai`, `kuotaTambahan`, `sisaKuota`, dan tanggal `expired`. Lifecycle sendiri — di-CRUD admin, di-import Excel, dan dipotong saat permohonan cuti tahunan disetujui. **Siklus-hidup**: kuota di-set/mulai **1 Juli** dan **expired 30 Juni tahun berikutnya** — karena itu boundary 30-Jun/1-Jul memisahkan dua siklus kuota, dan handler reservasi/settlement mem-bucket periode di tanggal itu (bukan tanggal arbitrer).

**Jenis Pengajuan** (EJenisPengajuanCuti):
Dua alur permohonan: **Pengajuan Cuti** (PENGAJUAN_CUTI — rencana cuti ke depan) dan **Klaim Cuti** (KLAIM_CUTI — mencatat cuti yang sudah terjadi, mis. sakit). Disimpan enum ordinal.

**Status Approval** (EApprovalCutiStatus):
Status keputusan atas Cuti Pegawai: PENDING, APPROVED, CONFIRMED, REJECTED, CANCELED, RETURNED — masing-masing berlabel Indonesia. Default PENDING saat dibuat.

**Approval Chain** (CutiApprovalChain):
Baris-baris rute persetujuan yang dibangkitkan saat permohonan dibuat — satu baris per jabatan yang harus menyetujui, urut per **Level Approval**. Tiap baris punya **Status Baca-Tulis** (EReadWriteStatus: NONE/READ/WRITE) yang menjadi pointer state-machine: WRITE = giliran jabatan ini memproses. Bukan `@Audited`, bukan soft-delete — turunan murni dari satu permohonan.

**Approval** (CutiApproval):
Catatan keputusan aktual seorang approver (pegawai + jabatan + level + status + catatan). Berbeda dari **Approval Chain**: Chain = rute yang direncanakan, Approval = jejak keputusan yang sudah diambil.

**Klaim Detail** (CutiKlaimDetail):
Satu baris per hari kerja yang diklaim pada alur **Klaim Cuti**. Bukan `@Audited`, bukan soft-delete.

**PIC Saat Ini** (picSaatIni):
Jabatan yang sedang berhak memproses permohonan pada titik tertentu di rantai approval — bergeser maju saat disetujui, mundur saat dikembalikan (RETURNED).
