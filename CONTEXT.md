# Kepegawaian — Master Context

Domain bahasa untuk sistem manajemen kepegawaian PERUMDAMTS. File ini menampung istilah yang bermakna bagi ahli domain, bukan detail implementasi. Diisi bertahap saat rewrite menyentuh tiap modul.

> **Rewrite in progress (worktree).** Folder utama (`rewrite/master-cqrs`) untuk kode baru; kode lama ada read-only di `../kepegawaian-legacy` (tag `legacy-snapshot`) sebagai referensi spec. Detail: [WORKTREE.md](WORKTREE.md).

> **Agent guidance:** Canonical ops guidance is in [CLAUDE.md](./CLAUDE.md) — includes GitNexus repo `kepegawaian`, build/run, architecture, issue tracking, and skills.

## Language

### Master (data referensi)

**Profesi**:
Peran/pekerjaan yang melekat pada seorang pegawai (mis. "Operator IPA"), terikat ke satu Organisasi, satu Jabatan, dan satu Grade.
_Avoid_: Posisi, role

**Jabatan**:
Kedudukan struktural dalam organisasi; pohon hierarkis (punya induk). Menentukan **Level**.
_Avoid_: Position (ambigu dengan Profesi)

**Level**:
Tingkatan/eselon yang melekat pada Jabatan, bukan diinput langsung di Profesi.

**Grade**:
Tingkat gaji/kepangkatan, dipilih langsung saat membuat Profesi.

**Organisasi**:
Unit kerja; pohon hierarkis (punya induk).

**APD** (Alat Pelindung Diri):
Perlengkapan keselamatan yang melekat pada sebuah Profesi (mis. helm, sarung tangan). Daftar pendek per Profesi.

**Alat Kerja**:
Peralatan kerja yang melekat pada sebuah Profesi. Daftar pendek per Profesi.
_Catatan domain_: APD dan Alat Kerja untuk satu Profesi selalu sedikit (segelintir item) — bukan daftar besar.

### Security (autentikasi & lingkungan)

**Lingkungan** (environment):
Mode jalan aplikasi yang menentukan apakah autentikasi diberlakukan. Dua nilai: **production** (perlu autentikasi) dan **development** (tanpa autentikasi). Dipilih lewat Spring profile, bukan flag runtime.

**Dev User** (principal statis):
Identitas tetap yang disuntikkan otomatis di **development** (`DEV`, role `ADMIN`+`SYSTEM`) supaya API bisa diuji tanpa token. Role-nya bisa ditimpa lewat `DEV_ROLES` untuk menguji jalur penolakan (403).
_Avoid_: "user palsu", "mock user" (ini principal nyata, hanya tidak diautentikasi)

**Appwrite JWT**:
Token yang diterbitkan Appwrite, divalidasi di **production** dengan memanggil Appwrite (`/account/jwt`) untuk mendapatkan **Appwrite User** beserta role-nya.

**Role**:
Hak akses pada principal (mis. `ADMIN`, `SYSTEM`). Menentukan endpoint mana yang boleh diakses (`@PreAuthorize`). Di Spring di-prefix `ROLE_`.

## Relationships

- Sebuah **Profesi** menunjuk tepat satu **Organisasi**, satu **Jabatan**, satu **Grade**.
- **Lingkungan** menentukan rantai keamanan: **production** memvalidasi **Appwrite JWT**; **development** memakai **Dev User** tanpa validasi.
- Sebuah **Appwrite User** / **Dev User** membawa satu atau lebih **Role**; **Role** menentukan akses endpoint.
- **Level** sebuah **Profesi** **diturunkan dari Jabatan-nya** (`profesi.level = jabatan.level`) — tidak diinput terpisah. Itu sebabnya form pembuatan Profesi tidak meminta `levelId`.
- Keunikan **Profesi** ditentukan oleh kombinasi `nama` + **Jabatan** + **Grade** (dasar cek duplikat).
- Membuat **Profesi** dengan kombinasi yang dulu pernah dihapus akan **menghidupkan kembali** record lama itu (bukan membuat data baru) — record yang terhapus tidak menghalangi pembuatan ulang.
- **Mengubah** sebuah **Profesi** agar kombinasinya sama dengan Profesi lain — baik yang aktif maupun yang sudah diarsip (dihapus) — **ditolak**. Menghidupkan kembali kombinasi arsip hanya lewat pembuatan ulang (create), bukan lewat edit.
- Keunikan **Organisasi** ditentukan oleh kombinasi `nama` + **parent** (dasar cek duplikat). Kode dan level TIDAK masuk kunci — `kode` adalah label tampilan (mis. '1.1.1') dan `level` adalah metadata struktur, bukan identitas.
- Membuat **Organisasi** dengan kombinasi nama+parent yang dulu pernah dihapus akan **menghidupkan kembali** record lama itu (bukan membuat data baru) — record yang terhapus tidak menghalangi pembuatan ulang.
- **Mengubah** sebuah **Organisasi** agar nama+parent-nya sama dengan Organisasi lain — baik yang aktif maupun yang sudah diarsip (dihapus) — **ditolak**. Menghidupkan kembali kombinasi arsip hanya lewat pembuatan ulang (create), bukan lewat edit.

## Example dialogue

> **Dev:** "Saat membuat **Profesi**, apakah user memilih **Level**?"
> **Domain expert:** "Tidak. **Level** ikut **Jabatan** yang dipilih — kalau Jabatan-nya berubah, Level-nya ikut. User hanya pilih Organisasi, Jabatan, dan Grade."

> **Dev:** "Kalau aku hapus **Profesi** 'Operator IPA', lalu nanti membuatnya lagi dengan Jabatan & Grade yang sama — itu data baru atau yang lama?"
> **Domain expert:** "Yang lama hidup lagi. Tidak ada duplikat — yang terhapus dipulihkan, lalu isinya diperbarui sesuai input baru."

> **Dev:** "Kalau aku **edit** Profesi 'Operator IPB' lalu mengubah Jabatan & Grade-nya supaya persis sama dengan 'Operator IPA' yang sudah ada — boleh?"
> **Domain expert:** "Tidak. Itu akan jadi dua hal yang sama. Edit harus ditolak. Kalau memang mau menghidupkan kombinasi yang pernah ada, buat ulang lewat tambah data — bukan lewat edit."

## Flagged ambiguities

- "Position" dipakai ambigu di kode lama untuk **Profesi** maupun **Jabatan** — keduanya konsep berbeda; gunakan istilah Indonesia (Profesi / Jabatan) untuk membedakan.
