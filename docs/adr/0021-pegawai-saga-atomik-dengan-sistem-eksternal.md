# Saga tulis Pegawai membungkus pemanggilan Appwrite di dalam satu transaksi DB

> **Status:** accepted — mengikat keputusan struktur Command di CONTEXT.md ("Keputusan rewrite sisi-tulis Pegawai").

`PegawaiCommandService.save(...)` adalah satu method `@Transactional` yang, dalam **satu transaksi DB**, mengerjakan berurutan: simpan/peta `Biodata`, resolve relasi, simpan `Pegawai`, buat `riwayat_sk` awal sesuai cabang status (genericKontrak / savePegawai+refSkPegawai+mkg / saveCapeg), **lalu** `authService.createUser(...)` ke **Appwrite** (sistem eksternal). Tiap cabang status adalah helper privat di kelas yang sama, bukan kelas Command/Step terpisah.

`createUser` diletakkan **paling akhir** dalam method, setelah semua tulis DB. Bila DB gagal sebelum titik itu, transaksi rollback dan Appwrite tak pernah tersentuh. Bila `createUser` gagal, exception melempar keluar dan transaksi DB rollback penuh.

## Considered Options

- **createUser di luar transaksi, setelah commit** (eventual): hindari orphan Appwrite saat DB rollback, tapi buka jendela sebaliknya — DB commit sukses lalu createUser gagal → pegawai ada tanpa akun. Perlu retry queue/outbox.
- **createUser di dalam transaksi, paling akhir + accept dual-write risk** (dipilih): satu unit kerja, kode lurus, tanpa infrastruktur outbox. Menerima satu sisa risiko: bila `createUser` **sukses** tapi `commit` DB sesudahnya **gagal**, akun Appwrite menjadi yatim (orphan) tanpa baris pegawai.
- **Two-phase / saga dengan kompensasi eksplisit** (deleteUser saat rollback): tutup orphan, tapi menambah kompleksitas kompensasi + penanganan kegagalan-kompensasi untuk frekuensi error yang rendah.

## Consequences

- **Dual-write tidak bisa benar-benar atomik.** DB dan Appwrite dua sistem berbeda; tak ada transaksi terdistribusi. Menaruh `createUser` paling akhir **memperkecil**, bukan menghapus, jendela orphan: hanya kegagalan commit DB **setelah** createUser sukses yang menyisakan akun yatim.
- **Mode kegagalan yang diterima:** akun Appwrite tanpa pegawai (orphan), bukan pegawai tanpa akun. Dipilih karena akun yatim lebih mudah dideteksi/dibersihkan belakangan daripada pegawai yang diam-diam tak bisa login. **Tidak ada kompensasi otomatis** di scope ini.
- **Tidak ada outbox/retry.** Bila pola ini terbukti menyisakan orphan yang mengganggu, langkah lanjut adalah outbox pattern atau kompensasi `deleteUser` — di-backlog, bukan sekarang.
- **`saveBatch` tetap waspada.** `saveBatch` `@Transactional` memanggil `save()`; karena `save()` lama menelan exception jadi status FAILED, rollback batch tak pernah terpicu. Pada rewrite, `save()` versi Command **harus melempar** agar `@Transactional` batch bisa rollback — jangan menelan exception di dalam saga.
- **Relasi:** melengkapi [ADR-0020](0020-pegawai-baca-tabel-lintas-modul-via-jooq.md) (baca lintas modul) dengan aturan arah tulis: menulis tetap satu transaksi pemilik, sistem eksternal dipanggil terakhir.
