# Pendidikan: `disetujui` role-conditional (auto-approve SDM) + guard DB `isLatest`

> **Status:** accepted — keputusan sesi grilling 2026-08-12 (grill-with-docs + domain-modeling), dari permintaan FE `docs/BE-REQUIREMENT-pendukung-pendidikan.md`.

## Konteks

FE (`kepegawaian-fe`) membangun konsol **Data Pendukung — Pendidikan** (`/profil/pendidikan`) dan butuh field `disetujui` di response untuk badge status. Fakta dari kode:

- Kolom `pendidikan.disetujui`, `disetujui_oleh`, `tanggal_disetujui`, `tanggal_pengajuan` **sudah ada sejak V1 baseline** (default `disetujui=0`) — tetapi entity JPA `Pendidikan` **tidak memetakannya**, DTO `PendidikanQuery` tidak mengeksposnya, dan semua write saat ini membiarkannya `0`.
- **Alur approval profil sudah ada**: `changedStatus` + `ProfileUpdateService`/`ProfileUpdatePendidikanApprovalService`. `ChangedStatusResolver`: penulis **SDM** → `changedStatus=false` (langsung stabil); penulis **pegawai/self-service** → `changedStatus=true` (masuk antrian persetujuan). Endpoint POST/PUT `/profil/pendidikan` **sama** untuk kedua peran — dibedakan di service layer, bukan endpoint.
- Normalisasi `isLatest` **sudah ada di jalur aplikasi** (`handleUpdateIsLatest()`): `isLatest=true` → semua baris lain milik biodata sama di-set false + `Biodata.pendidikanTerakhir` ikut disinkronkan via **bulk update** (`@Modifying @Query`), sengaja bukan `save()` supaya tidak memunculkan revisi Envers palsu di Biodata. Yang belum ada: pengaman level-DB terhadap race dua request bersamaan.
- Sibling `Keahlian` (pola yang dikutip FE) **inkonsisten**: POST → `disetujui=true`, PUT → `disetujui=false`, `tanggalDisetujui`/`disetujuiOleh` tidak pernah diisi (kolom mati).

## Keputusan

1. **`disetujui` di-set kondisional per-role** (server-side, bukan dari body request):
   - Penulis **SDM** (POST/PUT) → `disetujui=true` + stamp `tanggalDisetujui=now()` dan `disetujuiOleh=$id` user Appwrite.
   - Penulis **non-SDM** → `disetujui=false` + `changedStatus=true` (masuk antrian). Saat SDM **approve** di antrian → `disetujui=true` + stamp oleh approver. Saat **reject** → tetap `false`.
   - Request body **tidak** memuat field status; FE tidak boleh mengirimnya.
2. **`tanggalPengajuan=now()`** diisi saat **create dan update** (pola Keahlian; setiap perubahan non-SDM = pengajuan baru). Data lama yang belum pernah diisi tetap null.
3. **Backfill migration V29** untuk baris `is_deleted=0 AND changed_status=0` (stabil): `disetujui=1`, `tanggal_disetujui = COALESCE(created_at, updated_at)`, `disetujui_oleh = created_by`. Baris pending (`changed_status=1`) tetap `disetujui=0`.
4. **Guard level-DB `isLatest`**: generated column `is_latest_biodata` = `IF(is_latest=1 AND is_deleted=0, biodata_id, NULL)` + `UNIQUE(is_latest_biodata)` (MySQL membolehkan banyak NULL). Invarian "≤ 1 baris `true` per biodataId" dijamin DB bahkan saat race. Penyesuaian kode: `updateIsLatest`/`delete` ikut me-clear `is_latest` baris soft-deleted agar mayat record tidak memblokir guard.
5. **Pointer `Biodata.pendidikanTerakhir` dibiarkan** saat menjadi basi (PUT `true→false`, atau delete record terakhir) — sinkron hanya terjadi saat `isLatest=true` di-set; FE/HR yang menentukan pengganti. Keputusan HR selalu reversible (set `true` lagi → pointer ikut tersinkron).
6. **Scope: Pendidikan saja.** Inkonsistensi `Keahlian`/`PengalamanKerja`/`Pelatihan`/`LampiranSk` (pola approval setengah mati) dicatat sebagai tech debt terpisah.
7. `seedFromBiodata` (baseline saat Biodata dibuat) → `disetujui=true` (data baseline dipercaya).

## Considered Options

- **Auto-true pada semua POST/PUT (literal permintaan FE)** (ditolak): paling sederhana, tetapi record self-service yang masih pending akan tampil "Disetujui" — badge bohong; bertentangan dengan pernyataan FE sendiri di dokumen ("data di luar konsol admin boleh `disetujui=false`"). Kondisional per-role (dipilih) memuaskan kedua sisi sekaligus.
- **Tanpa guard DB `isLatest`** (ditolak): normalisasi aplikasi sudah transaksional per-request, tapi dua request bersamaan bisa menghasilkan 2 baris `true` dan mengorup join laporan/dashboard yang bergantung `is_latest=1 AND is_deleted=0`. Guard murah (satu migration) dan membuat invarian tidak bergantung disiplin aplikasi.
- **Guard tanpa penyesuaian soft-delete** (ditolak): baris terhapus yang masih `is_latest=1` (delete tidak me-clear) akan memblokir write baru — guard hanya aman jika baris soft-deleted ikut di-clear.
- **Clear pointer `pendidikanTerakhir` saat basi** (ditolak): berisiko menghapus data jenjang yang masih dipakai laporan/statistik saat HR belum menunjuk pengganti; "biarkan" (dipilih) lebih aman dan reversible.
- **Benerin Keahlian dkk sekaligus** (ditolak untuk task ini): scope membengkak; dicatat sebagai tech debt.

## Consequences

- **FE**: regenerate tipe (`node docs/api/extract-types.js`) → `disetujui`, `tanggalPengajuan`, `tanggalDisetujui`, `disetujuiOleh` masuk `PendidikanQuery`; badge jujur untuk semua status.
- **BE — implementasi butuh**: tambah 4 field di entity `Pendidikan`; tambah field di `PendidikanQuery` + `PendidikanSelects` + `PendidikanJooqMapper` + `PendidikanMultisetJooqMapper`; auto-set + stamp di `PendidikanCommandService` (create/update/seed) & `ProfileUpdatePendidikanApprovalService` (approve/reject); migration V29 (backfill + generated column guard); penyesuaian `PendidikanRepository` (clear `is_latest` saat delete / cakup baris deleted); regenerasi jOOQ (ADR-0004/0012).
- **Tidak ada perubahan skema untuk kolom `disetujui`** — sudah ada sejak baseline.
- **`isLatest`**: guard hanya membatasi baris **aktif** (`is_deleted=0`); mayat record bebas mempertahankan nilai lamanya asalkan `is_latest`-nya di-clear saat dihapus.
- **Keahlian dkk**: tetap berperilaku lama; issue tech debt terpisah.
