# CONTEXT-MAP — Kepegawaian

Index lazy-read domain context. **Jangan membaca semua file sekaligus** — pilih hanya yang relevan dengan tugas.

## Cara Pakai

1. Baca tabel di bawah untuk menemukan sub-context yang relevan.
2. Baca **hanya** file tersebut.
3. Jika tugas menyentuh beberapa modul, baca sub-context masing-masing.

## Peta Sub-Context

| Jika tugas menyentuh... | Baca file ini |
|-------------------------|---------------|
| Modul `master/` — terminologi (Profesi, Jabatan, Organisasi, Grade, Level, APD) | [`docs/context/language-master.md`](docs/context/language-master.md) |
| Modul `master/` — keputusan CQRS cleanup (mapper, fetchInto, enum, FK) | [`docs/context/decisions-master.md`](docs/context/decisions-master.md) |
| Modul `pegawai/` — terminologi (NIPAM, Status, SK, Ringkasan, JSON baca) | [`docs/context/language-pegawai.md`](docs/context/language-pegawai.md) |
| Modul `pegawai/` — keputusan rewrite & arsitektur | [`docs/context/decisions-pegawai.md`](docs/context/decisions-pegawai.md) |
| Modul `kepegawaian/` (SK, Mutasi, Kontrak, Terminasi, SP) — keputusan | [`docs/context/decisions-pegawai.md`](docs/context/decisions-pegawai.md) |
| Modul `profil/` (biodata, pendidikan, keahlian, keluarga, updateProfile) | [`docs/context/language-profil.md`](docs/context/language-profil.md) |
| Modul `cuti/` — terminologi (Cuti Pegawai, Approval Chain, Kuota, PIC) | [`docs/context/language-cuti.md`](docs/context/language-cuti.md) |
| Modul `cuti/` — keputusan rewrite & arsitektur | [`docs/context/decisions-cuti.md`](docs/context/decisions-cuti.md) |
| Modul `penggajian/` — terminologi (Dasar Gaji, Batch Gaji, Status Proses, Potongan Tambahan) | [`docs/context/language-penggajian.md`](docs/context/language-penggajian.md) |
| Modul `penggajian/` — rewrite CQRS/JOOQ (claim order + ADR) | [`docs/penggajian-cqrs-claim-order.md`](docs/penggajian-cqrs-claim-order.md), [`docs/adr/0024-gajibatchroot-kafka-diisolasi-ke-eventpublisher.md`](docs/adr/0024-gajibatchroot-kafka-diisolasi-ke-eventpublisher.md) |
| Auth, JWT, Spring profile, `@PreAuthorize`, Dev User | [`docs/context/language-security.md`](docs/context/language-security.md) |
| Relasi antar domain, dependency lintas-modul, arah coupling | [`docs/context/relationships.md`](docs/context/relationships.md) |
| Migrasi data dari legacy SmartOffice ke Kepegawaian Baru | [`docs/context/language-migrasi.md`](docs/context/language-migrasi.md), [`docs/adr/0044-staged-batch-etl-runner-migrasi-legacy.md`](docs/adr/0044-staged-batch-etl-runner-migrasi-legacy.md) |
| Contoh percakapan domain expert, istilah ambigu | [`docs/context/examples-and-flags.md`](docs/context/examples-and-flags.md) |

## Sub-Context Files

| File | Ukuran perkiraan | Isi |
|------|-----------------|-----|
| [`language-master.md`](docs/context/language-master.md) | ~3 KB | Glossary modul master |
| [`decisions-master.md`](docs/context/decisions-master.md) | ~4 KB | Keputusan rewrite CQRS cleanup modul master |
| [`language-pegawai.md`](docs/context/language-pegawai.md) | ~5 KB | Glossary modul pegawai |
| [`language-profil.md`](docs/context/language-profil.md) | ~5 KB | Glossary modul profil |
| [`language-cuti.md`](docs/context/language-cuti.md) | ~4 KB | Glossary modul cuti |
| [`language-penggajian.md`](docs/context/language-penggajian.md) | ~4 KB | Glossary modul penggajian (payroll & batch) |
| [`language-migrasi.md`](docs/context/language-migrasi.md) | ~2 KB | Glossary migrasi data SmartOffice ke DB baru |
| [`language-security.md`](docs/context/language-security.md) | ~2 KB | Glossary auth/security |
| [`relationships.md`](docs/context/relationships.md) | ~5 KB | Relasi & arah dependency |
| [`decisions-pegawai.md`](docs/context/decisions-pegawai.md) | ~10 KB | Keputusan rewrite pegawai & kepegawaian |
| [`decisions-cuti.md`](docs/context/decisions-cuti.md) | ~10 KB | Keputusan rewrite cuti |
| [`examples-and-flags.md`](docs/context/examples-and-flags.md) | ~1 KB | Contoh dialog & ambiguitas |

> **Catatan produser**: tambahkan sub-context baru di sini saat modul baru ditambah atau domain baru diperluas. Ikuti pola penamaan `language-<modul>.md` (glossary) dan `decisions-<modul>.md` (keputusan arsitektur/rewrite).
