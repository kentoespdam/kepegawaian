# 0060: Legacy-Compatible Organisasi Code

- **Status**: Accepted
- **Context**: Kebutuhan sinkronisasi dan migrasi data organisasi dari aplikasi legacy (`smartoffice.organization`) dan tools migrasi (`tools/migration/stages/stage1_master.py`). Kode organisasi baru sebelumnya menggunakan dot-notation (`0`, `1`, `1.1`), sedangkan legacy dan tools migrasi menggunakan `org_code` alfanumerik (`DPW`, `D1`, dll).
- **Decision**:
  1. Menyelaraskan field `kode` pada entitas Organisasi dengan `org_code` legacy untuk menjaga interoperabilitas migrasi.
  2. Mempertahankan field `shortName` sebagai display alias opsional.
  3. Menerapkan `@NotBlank` dan `@Size(max = 64)` pada `OrganisasiPostRequest`/`OrganisasiPutRequest` tanpa batasan regex ketat.
  4. Tidak menerapkan constraint UNIQUE ketat di DB pada `kode` karena adanya duplikasi historis legacy (`'B1.3'`, `'BA8.2'`); keunikan tetap dijamin oleh kombinasi `nama` + `parentId`.
  5. Memperbarui `V3__seed_organisasi.sql` secara langsung untuk lingkungan development.
- **Consequences**:
  - Positif: Kemudahan migrasi 1:1, kompatibel penuh dengan `stage1_master.py`.
  - Negatif / Mitigasi: Format kode tidak lagi mencerminkan pohon hierarki secara visual (pohon hierarki tetap diandalkan via `parentId` dan `levelOrg`).
