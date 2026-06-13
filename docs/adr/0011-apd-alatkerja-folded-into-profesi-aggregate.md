# APD & Alat Kerja: punya endpoint tulis sendiri, tapi tanpa endpoint baca standalone

Di kode lama, `Apd` dan `AlatKerja` masing-masing punya controller CRUD penuh berdiri sendiri (`/master/apd`, `/master/alat-kerja`) — POST/PUT/DELETE **dan** GET index/list/{id} — tiap row membawa `profesiId` di body, diperlakukan sebagai entity master independen. Secara domain (CONTEXT.md) keduanya adalah daftar pendek yang **melekat pada sebuah Profesi** ("terkunci di profesi"): selalu ditampilkan bersama Profesi-nya, bukan sebagai layar master tersendiri. Sisi baca rewrite sudah mengunci ini — APD & Alat Kerja di-embed ke `ProfesiDetail` lewat JOOQ MULTISET.

Keputusan (CQRS asimetris):

- **Sisi tulis (command) TETAP punya endpoint per-anak:** CREATE, UPDATE, DELETE untuk tiap APD / Alat Kerja, masing-masing beroperasi pada satu row ber-id. FE butuh memutasi satu item tanpa mengirim ulang seluruh daftar.
- **Sisi baca (query) TIDAK punya endpoint standalone:** tak ada GET `index`/`list`/`/{id}` untuk APD/Alat Kerja. Mereka muncul HANYA di `ProfesiDetail` (MULTISET), karena di FE memang selalu tampil bersama Profesi.

## Considered Options

### Model APD & Alat Kerja di rewrite

- **CRUD penuh standalone** (ditolak): paritas kode lama, termasuk GET index/list/{id}. Ditolak karena tidak ada layar/konsumen yang membaca APD lepas dari Profesi-nya — endpoint baca standalone jadi permukaan mati yang harus dipelihara, dan menduakan sisi baca yang sudah meng-embed keduanya di `ProfesiDetail`.
- **Dilipat penuh jadi child-list replace-all di tulis Profesi** (ditolak): satu PUT Profesi mengganti seluruh list anak. Ditolak karena FE perlu menambah/mengubah/menghapus satu item APD tanpa mengirim ulang seluruh daftar; replace-all memaksa klien mengelola state list penuh.
- **Tulis per-anak, baca via Profesi** (dipilih): command endpoint CREATE/UPDATE/DELETE per-row tetap ada (mutasi granular satu item), tapi tak ada endpoint baca standalone (baca hanya lewat `ProfesiDetail` MULTISET). Cocok dengan domain (anak terkunci di Profesi, selalu tampil bersamanya) sekaligus dengan kebutuhan FE memutasi satu item.

## Consequences

- CQRS terlihat asimetris di sini: jumlah command endpoint ≠ jumlah query endpoint untuk entity ini. Itu disengaja — sisi tulis butuh granular, sisi baca cukup lewat induk.
- DELETE per-anak memakai soft-delete (`is_deleted = true`) sesuai aturan global repo; MULTISET di `ProfesiDetail` menyaring `is_deleted = false`. (`Apd`/`AlatKerja` mewarisi kolom `is_deleted` dari base; di rewrite mereka butuh `@SQLDelete`/`@SQLRestriction` sendiri seperti entity master lain — di kode lama soft-delete ditegakkan di service standalone yang kini tetap ada di sisi tulis.)
- Endpoint tulis anak **bersarang di bawah induk**: `POST /master/profesi/{profesiId}/apd`, `PUT /master/profesi/{profesiId}/apd/{id}`, `DELETE /master/profesi/{profesiId}/apd/{id}` (analog untuk `alat-kerja`). URL mengekspresikan kepemilikan: tak bisa membuat APD tanpa menyebut Profesi-nya. `profesiId` diambil dari **path**, bukan body — menghapus mode-gagal "profesiId di body tidak cocok". Parent wajib ada (resolve via getReferenceById → 409 bila FK buruk, ADR-0008). Ditolak: resource datar `/master/apd` dengan `profesiId` di body (paritas klien lama) — kepemilikan jadi konvensi tersembunyi di payload, bukan struktural.
- Mapper anak tetap pure function: command service me-resolve & memvalidasi Profesi induk, lalu menyerahkan ke mapper.
- Migration `V1_0_0__create_master.sql` harus menambahkan tabel `apd` & `alat_kerja` (saat ini belum ada): id, `profesi_id` FK, nama, plus kolom audit + `is_deleted` standar master.
- Tak ada `ApdQueryService`/`AlatKerjaQueryService` standalone; hanya `ApdCommandService`/`AlatKerjaCommandService`. Pembacaan ditangani query-side Profesi.
