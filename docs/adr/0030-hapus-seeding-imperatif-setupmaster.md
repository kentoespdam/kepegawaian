# Hapus jalur seeding imperatif `setupMaster/`, seeding data via Flyway

Data master (referensi) di-*seed* lewat migration Flyway `V3_0_*` — sumber kebenaran tunggal — bukan lewat endpoint `GET /setup-master`. Seluruh paket `services/setupMaster/` (24 service + interface `SetupMaster`) dan `SetupMasterController` dihapus karena redundan dengan seeder Flyway yang sudah ada.

Ini konsekuensi turunan dari [ADR 0002](0002-flyway-schema-source-of-truth.md): jika Flyway memiliki *schema*, ia juga memiliki *data referensi* yang menyertainya — dua jalur seeding untuk data yang sama adalah sumber drift.

## Considered Options

- **Pertahankan endpoint `GET /setup-master`** sebagai fallback re-seed manual (ditolak): user konfirmasi tidak butuh; seeding imperatif tidak ter-version, tidak idempoten via checksum, dan bisa dipicu ulang kapan saja menimbulkan drift diam-diam vs migration.
- **Hapus total, seeding hanya via Flyway** (dipilih): deklaratif, versioned, idempoten (checksum), deterministik dalam rantai migration → DB → JOOQ codegen.

## Consequences

- Tiap master yang dulu di-seed paket ini punya padanan Flyway: DetailDasarGaji=`V3_0_7..12`, Level/Golongan/Grade=`V3_0_0`, Organisasi=`V3_0_1`, Jabatan=`V3_0_2/4`, JenisKeahlian/Kitas/Pelatihan/JenjangPendidikan=`V3_0_5`, JenisSp/Sanksi=`V3_0_6`, GajiKomponen=`V3_0_14..16`, PendapatanNonPajak/GajiProfil=`V3_0_13`, RumahDinas/GajiTunjangan=`V3_0_17`, GajiPotonganTkk/GajiParameter=`V3_0_18`, PrefRole/CutiJenis=`V3_0_19`, Profesi=`V3_0_20/22`, AlasanBerhenti/DasarGaji=`V3_0_21`/`V3_0_6_1`.
- Blast-radius nol: 25 kelas `services/setupMaster/*` hanya dikonsumsi `SetupMasterController` — tidak ada domain lain yang menyentuhnya.
- `SetupDetailDasarGaji` (567 baris) **dihapus**, bukan di-refactor agar patuh batas 120 baris (CODING_RULES §4). Data referensi baru = tambah/ubah migration `V3_0_*`, bukan tambah kode Java.
- Eksekusi dilacak di bd `kepegawaian-rvw` (child epic `kepegawaian-be8`).
