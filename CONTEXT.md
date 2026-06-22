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

### Profil (self-service data pegawai)

**Profil**:
Data pribadi yang dimiliki & dikelola pegawai sendiri — biodata, pendidikan, keahlian, keluarga, pelatihan, pengalaman kerja, kartu identitas, beserta lampiran. Berbeda dari Master (data referensi milik admin): Profil dimiliki pegawai dan perubahannya melewati persetujuan.

**Pengajuan Perubahan** (ProfileUpdate):
Catatan bahwa pegawai mengubah salah satu data Profil-nya dan menunggu keputusan admin. Setiap pengajuan menunjuk satu baris data (`revId`), satu **Jenis Aksi** (tambah/ubah/hapus), dan satu tabel Profil yang terkena.
_Avoid_: "audit", "log" — ini bukan jejak pasif, melainkan antrian persetujuan yang menggerakkan revert.

**Jenis Aksi** (actionType): tambah (INSERT), ubah (UPDATE), atau hapus (DELETE) — menentukan perilaku saat **ditolak**.

**Pendidikan Terlatest** (`isLatest`) & **Pendidikan Terakhir** (`pendidikanTerakhir`):
Seorang pegawai punya banyak baris **Pendidikan**; tepat satu ditandai sebagai yang terkini (`isLatest=true`). **Pendidikan Terakhir** adalah field turunan (denormalisasi) di **Biodata** — jenjang dari Pendidikan yang `isLatest`-nya `true`. Ia disimpan di Biodata sebagai jalan pintas baca (mis. di ringkasan pegawai), bukan sumber kebenaran tersendiri.
_Catatan domain_: nilai ini **diturunkan**, bukan diinput bebas. Menandai satu Pendidikan `isLatest=true` otomatis menyingkirkan tanda itu dari baris Pendidikan lain milik pegawai yang sama, lalu menyalin jenjangnya ke `Biodata.pendidikanTerakhir`.

**Status Berubah** (changedStatus):
Penanda pada baris data Profil bahwa ada perubahan yang **belum disetujui**. `true` = menunggu keputusan; `false` = stabil/disetujui. Hanya perubahan dengan `changedStatus=true` yang memunculkan Pengajuan Perubahan.
Nilainya **ditentukan server berdasarkan role**, bukan dikirim client: edit oleh **SDM** (petugas kepegawaian) langsung stabil (`changedStatus=false`, tanpa Pengajuan Perubahan); edit oleh **pegawai** menjadi menunggu (`changedStatus=true`, memunculkan Pengajuan Perubahan). Role dibaca dari principal Appwrite (`AppwriteUser.getAuthorities()` → `ROLE_*`).
_Avoid_: menyamakan `changedStatus` dengan riwayat. `changedStatus` hanya menggerbang **Antrian Persetujuan**, bukan **Riwayat Perubahan** (lihat di bawah) — riwayat selalu dicatat di setiap tulis, apa pun nilai `changedStatus`.

**Riwayat Perubahan** (Envers) vs **Antrian Persetujuan** (ProfileUpdate) — dua lapis terpisah:
- **Riwayat Perubahan**: entity Profil ber-`@Audited`, jadi **setiap** tulis (`save`) menghasilkan satu revisi Envers — tanpa peduli role maupun `changedStatus`. Riwayat tidak pernah hilang.
- **Antrian Persetujuan**: baris **Pengajuan Perubahan** hanya dibuat saat `changedStatus=true`. Ini daftar tunggu keputusan SDM, bukan riwayat.
- Konsekuensi: edit oleh **SDM** (`changedStatus=false`) **tetap** punya riwayat Envers penuh; yang dilewati hanya antrian persetujuan (SDM tak perlu menyetujui dirinya sendiri).
- Itu sebabnya `changed_status` dari body client adalah **bug keamanan**: ia tak mengontrol riwayat (Envers tetap jalan), melainkan membiarkan pegawai biasa menyelundupkan perubahan langsung ke status stabil tanpa di-acc SDM (bypass persetujuan).

**Disetujui / Ditolak**:
Keputusan admin atas Pengajuan Perubahan.
- **Disetujui**: baris ditandai stabil (`changedStatus=false`); nilai yang diajukan dipertahankan.
- **Ditolak**: dikembalikan menurut Jenis Aksi —
  - tambah ditolak → baris dihapus (tak pernah sah ada);
  - ubah ditolak → **dikembalikan ke revisi sebelumnya** (dua revisi Envers terakhir dibaca, nilai revisi lama ditulis ulang);
  - hapus ditolak → batal hapus (baris diaktifkan kembali).

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
- Setiap tulis ke data **Profil** menghasilkan satu **Riwayat Perubahan** (revisi Envers) — selalu, apa pun role. Hanya tulis dengan `changedStatus=true` yang **juga** memunculkan **Pengajuan Perubahan**.
- Menulis sebuah **Pendidikan** dengan `isLatest=true` menyinkronkan **Pendidikan Terakhir** di **Biodata** (menyingkirkan `isLatest` dari baris lain + menyalin jenjang ke `Biodata.pendidikanTerakhir`). Sinkronisasi ini dilakukan via **bulk update** (`@Modifying @Query`), bukan `save()` entity terkelola — sengaja, supaya tidak memunculkan **Riwayat Perubahan** Envers palsu pada Biodata untuk perubahan yang tidak pernah diajukan/disetujui siapa pun. (Pendidikan Terakhir adalah field turunan; riwayatnya yang sah ada di baris Pendidikan, bukan di Biodata.)
- **Role** penulis menentukan `changedStatus`: **SDM** → `false` (langsung stabil), **pegawai** → `true` (menunggu). Keputusan ini diambil **server** dari principal, bukan dari body request.
- Membuat sebuah **Biodata** **otomatis menyemai (seed) dua data anak awal**: satu **Kartu Identitas** kosong dan satu baris **Pendidikan** `isLatest=true` (jenjangnya diambil dari `pendidikanTerakhir` yang diisi saat pembuatan Biodata). Kedua seed ini **lahir oleh sistem**, bukan pengajuan pegawai — maka `changedStatus`-nya **`false`** (langsung stabil) dan **tidak** memunculkan **Pengajuan Perubahan** (tak ada yang perlu menyetujui data yang dibuat sistem sendiri). Seed tetap menghasilkan **Riwayat Perubahan** Envers seperti tulis biasa, dan keseluruhan pembuatan Biodata + dua seed berada dalam **satu transaksi** (gagal salah satu → batal semua). Aktor seed adalah **sistem**, jadi `changedStatus=false`-nya ditetapkan **eksplisit**, bukan lewat penentuan berbasis role.
- Pembacaan data **Profil** selalu **terikat pada satu pegawai** (`nik`, dibawa field `biodataId` pada query): seorang pegawai hanya membaca Profil miliknya sendiri, dan SDM membaca Profil milik **satu** pegawai pada satu waktu — tidak ada layar "semua Profil lintas pegawai". Karena itu sisi baca (Query) menjadikan `biodataId` sebagai field **wajib** (`@NotBlank`) pada IndexQuery — bukan filter opsional — dan controller `index` **wajib** memvalidasinya (`@Valid @ParameterObject`), berbeda dari master yang membiarkan param query tanpa validasi. Dengan begitu predikat `WHERE biodata.nik = ?` dijamin ada sebelum query JOOQ jalan, sehingga tiap query selalu terbatas pada satu pegawai dan tak pernah memindai seluruh tabel. (Pembacaan satu baris lewat `id` tetap ada untuk detail; pengecekan kepemilikan baris-tunggal diserahkan ke RBAC per-entity, belum diimplementasikan.) **Pengecualian: Biodata.** Biodata bukan baris anak — ia *adalah* pegawai (kunci `NIK`), tak punya induk untuk diacu. Daftar Biodata global adalah **pencarian direktori** yang sah bagi SDM (filter `nik`/`nama`/`jenisKelamin`/`alamat`/`isPegawai`), bukan kebocoran — maka Biodata mengikuti bentuk global seperti master (filter opsional, tanpa scope wajib), sedangkan 7 entitas anak tetap wajib `biodataId`.
- Perilaku **membuat ulang data Profil yang pernah dihapus** berbeda per jenis data, sesuai apakah data itu punya kunci alami:
  - **Pendidikan** (kunci: pegawai + jenjang + tahun masuk) dan **Kartu Identitas** (kunci: NIK + jenis kartu) — membuat ulang kombinasi yang pernah dihapus **menghidupkan kembali** baris lama itu (sama seperti **Profesi**/**Organisasi** di master), bukan membuat baris baru. Tanpa ini, baris terhapus tetap menempati kunci dan membuat user tak bisa menambah ulang data yang ia hapus. Penambahan-ulang ini tetap berstatus **tambah** (`INSERT`): bila pegawai yang melakukannya dan **SDM menolak**, baris dihapus permanen (diperlakukan sebagai tambah yang ditolak — bukan dikembalikan ke status arsip), sehingga kuncinya bebas lagi untuk dicoba ulang. Riwayat Envers lama baris itu tetap tersimpan di tabel audit.
  - **Keluarga** (ProfilKeluarga) — baris aktif dan baris arsip **boleh berdampingan**; menambah ulang setelah hapus adalah baris baru, bukan menghidupkan yang lama. Hanya duplikat **aktif** persis yang ditolak.
  - **Keahlian**, **Pelatihan**, **Pengalaman Kerja**, **Lampiran Profil** — tak punya kunci alami (riwayat pribadi yang menumpuk); tiap penambahan selalu baris baru, tak ada konsep "menghidupkan kembali".
- Sisi tulis Profil tetap memicu pembuatan **Pengajuan Perubahan** (memanggil `profileUpdateService.create(...)`), tetapi seluruh logika **Disetujui/Ditolak** dan revert milik **modul updateProfile** — ketergantungan satu arah: profil → updateProfile. (Catatan: penentuan `changedStatus` berbasis role saat ini dilakukan via `@PreAuthorize`/pengecekan role tunggal; rencananya digantikan **RBAC spesifik per-entity** — hak akses per data Profil, belum diimplementasikan.)

## Example dialogue

> **Dev:** "Saat membuat **Profesi**, apakah user memilih **Level**?"
> **Domain expert:** "Tidak. **Level** ikut **Jabatan** yang dipilih — kalau Jabatan-nya berubah, Level-nya ikut. User hanya pilih Organisasi, Jabatan, dan Grade."

> **Dev:** "Kalau aku hapus **Profesi** 'Operator IPA', lalu nanti membuatnya lagi dengan Jabatan & Grade yang sama — itu data baru atau yang lama?"
> **Domain expert:** "Yang lama hidup lagi. Tidak ada duplikat — yang terhapus dipulihkan, lalu isinya diperbarui sesuai input baru."

> **Dev:** "Kalau aku **edit** Profesi 'Operator IPB' lalu mengubah Jabatan & Grade-nya supaya persis sama dengan 'Operator IPA' yang sudah ada — boleh?"
> **Domain expert:** "Tidak. Itu akan jadi dua hal yang sama. Edit harus ditolak. Kalau memang mau menghidupkan kombinasi yang pernah ada, buat ulang lewat tambah data — bukan lewat edit."

## Flagged ambiguities

- "Position" dipakai ambigu di kode lama untuk **Profesi** maupun **Jabatan** — keduanya konsep berbeda; gunakan istilah Indonesia (Profesi / Jabatan) untuk membedakan.
