# Snapshot label master pada Riwayat menuntut findById, bukan getReferenceById

Pada modul kepegawaian, sebagian aggregate menyalin **label** master (bukan sekadar FK) ke kolom denormalisasi saat tulis. `RiwayatMutasi` & `RiwayatTerminasi` melakukan `setNamaGolongan(golongan.getPangkat() + " - " + golongan.getGolongan())`, `setNamaOrganisasi(organisasi.getNama())`, `setNamaJabatan(jabatan.getNama())`, `setNamaProfesi(profesi.getNama())` — plus seluruh varian `*Lama`. Untuk membaca nilai itu, entitas master **harus ter-hidrasi**. Maka Command kedua aggregate ini me-resolve master via **`findById(id).orElseThrow(...)`**, bukan `getReferenceById`.

Ini **bukan pelanggaran [ADR-0008](0008-fk-via-getreference-on-write.md)**. ADR-0008 mengatur *attach FK relasi* (set proxy lalu flush → nol SELECT). Di sini kita butuh **data**, bukan sekadar FK: memanggil `.getNama()` pada proxy `getReferenceById` tetap memicu lazy-load SELECT saat snapshot, jadi proxy tidak menghemat apa pun dan justru berisiko `LazyInitializationException` bila session sudah tutup. `findById` adalah pilihan jujur: satu SELECT eksplisit per master, error jelas saat lookup (id berasal dari input user), nilai siap disnapshot.

Pembatas (kriteria pindah jalur):

- **FK murni → `getReferenceById`** (ADR-0008). Contoh: `RiwayatSk` yang `toEntity` hanya `setGolongan(golongan)` tanpa baca label apa pun.
- **Snapshot label → `findById`** (ADR ini). Contoh: `RiwayatMutasi`, `RiwayatTerminasi`.

Yang dibuang di kedua kasus: utilitas lama `DetailFromList.findExist*` yang memuat **seluruh** tabel master via `findAll()` lalu mencari di memori (3-4 full-table-scan untuk mengambil 2-3 baris). Diganti `findById` per-id pada jalur snapshot, dan `getReferenceById` per-id pada jalur FK murni.

## Consequences

- Reviewer yang mengejar kepatuhan ADR-0008 mungkin tergoda "memperbaiki" `findById` di Mutasi/Terminasi menjadi `getReferenceById`. ADR ini mencegahnya: snapshot label butuh entitas ter-load — perubahan itu akan memecah denormalisasi diam-diam atau memunculkan lazy-load tersembunyi.
- Bila kelak kolom `Nama*` denormalisasi dihapus (label dipindah ke proyeksi baca JOOQ), aggregate terkait bisa turun ke `getReferenceById` dan ADR ini di-*supersede*.
