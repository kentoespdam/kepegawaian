# Attach FK relasi via getReferenceById, bukan findById

Sisi Command (tulis) menempelkan relasi `@ManyToOne` (Organisasi, Jabatan, Grade) ke entitas baru/diubah dengan **`JpaRepository#getReferenceById(id)`** — proxy lazy, **tanpa SELECT**. Kode lama melakukan `findById(id).orElseThrow(() -> new RuntimeException("Unknown X"))` per FK (tiga round-trip SELECT pada setiap create/update) hanya untuk men-set foreign key.

```java
Profesi p = new Profesi();
p.setOrganisasi(organisasiRepository.getReferenceById(req.organisasiId()));
p.setJabatan(jabatanRepository.getReferenceById(req.jabatanId()));
p.setGrade(gradeRepository.getReferenceById(req.gradeId()));
// flush → satu INSERT, nol SELECT
```

Best practice diverifikasi via context7 (panduan performa Spring Boot/Hibernate, `/andreipall/spring-boot-jpa`, rep High): mengambil parent lewat SELECT hanya untuk men-set FK adalah "a performance penalty and a pointless action, because Hibernate can set the underlying foreign key value for an uninitialized proxy". Mekanisme yang dianjurkan: `EntityManager#getReference()` / `JpaRepository#getReferenceById` — set proxy sebagai relasi lalu save hanya memicu INSERT.

FK yang tidak ada muncul sebagai `DataIntegrityViolationException` saat flush, dipetakan `@RestControllerAdvice` ke **409**. Constraint FK di DB sudah menjadi penjaga kebenaran; pre-check SELECT redundan.

Catatan: kode lama me-resolve organisasi/jabatan/grade tetapi **tidak** `level`, walau `levelId` ada sebagai field filter. Sisi tulis rewrite mengikuti FK riil entitas (lihat skema), bukan field filter — `level` tidak ditempel kecuali kolomnya memang ada di tabel.

## Considered Options

- **`findById` per FK** (ditolak): tiga SELECT ekstra tiap create/update, tetapi memungkinkan `ResourceNotFoundException("Unknown Jabatan")` → 404 yang menyebut FK persis sebelum menyentuh DB. Presisi pesan error lebih baik, cocok perilaku lama. Biaya: round-trip untuk data yang sudah dijaga constraint FK.
- **`getReferenceById`** (dipilih): nol SELECT, satu INSERT. Sesuai best practice Hibernate dan KISS. Trade-off: pesan error FK invalid menjadi generik (constraint violation tidak menyebut FK mana). Diterima — endpoint master write admin-only, frekuensi rendah.

## Consequences

- Pesan error FK invalid generik (409 "invalid reference"), tidak menyebut kolom FK spesifik. Bila kelak butuh presisi, validasi eksplisit bisa ditambah per-kasus — tidak diantisipasi di muka (YAGNI).
- Menyimpang dari kode lama: porting harus mengganti pola `findById...orElseThrow(RuntimeException)` dengan `getReferenceById`.
- `DuplicateResourceException`/`ResourceNotFoundException` typed tetap dipakai untuk kasus lain (duplikat unik, entitas target update tak ada) — keputusan ini hanya soal **attach FK relasi**, bukan seluruh error handling.
- Berlaku untuk semua service Command di semua modul rewrite.
