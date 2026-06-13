# Profesi.level tetap denormalisasi — sisi tulis memuat Jabatan penuh

`Profesi` punya kolom FK `level_id`, tetapi nilainya **tidak** berasal dari field request. Kode lama menurunkannya dari Jabatan: `entity.setLevel(jabatan.getLevel())` (`ProfesiPutRequest.toEntity`). `profesi.level_id` selalu salinan denormalisasi dari `jabatan.level_id`.

Rewrite mempertahankan perilaku dan skema ini: sisi Command memuat **Jabatan penuh** lewat `findById` (SELECT riil), membaca `jabatan.getLevel()`, lalu menyimpan `level_id` di Profesi.

```java
// WRITE (Command) — pengecualian ADR-0008 untuk FK ini saja
Jabatan jab = jabatanRepository.findById(req.jabatanId())
        .orElseThrow(ResourceNotFoundException::new); // SELECT riil
p.setOrganisasi(organisasiRepository.getReferenceById(req.organisasiId())); // proxy, 0 SELECT
p.setJabatan(jab);
p.setLevel(jab.getLevel());   // denormalisasi
p.setGrade(gradeRepository.getReferenceById(req.gradeId()));               // proxy, 0 SELECT
```

## Hubungan dengan ADR-0008

ADR-0008 menetapkan attach FK via `getReferenceById` (proxy, nol SELECT). ADR ini adalah **pengecualian terbatas untuk satu FK** (`jabatan` pada `Profesi`): karena `level` diturunkan dari `jabatan.getLevel()`, Jabatan tidak bisa tetap proxy — memanggil `.getLevel()` pada proxy yang belum diinisialisasi tetap memicu SELECT. Maka Jabatan dimuat eksplisit via `findById`, dan kegagalan memberi `ResourceNotFoundException` → 404 yang menyebut Jabatan (lebih presisi dari 409 generik). Organisasi dan Grade tetap proxy sesuai ADR-0008.

## Considered Options

- **Turunkan saat baca, buang kolom** (ditolak): hentikan penyimpanan `level_id`; Jabatan tetap proxy murni (ADR-0008 utuh); sisi baca JOOQ join Profesi→Jabatan→Level (sudah dilakukan, ADR-0001) untuk mengekspos level; filter `levelId` lewat `jabatan.level_id` melalui join. Menghapus kolom denormalisasi yang bisa drift. Tetapi migrasi harus DROP `profesi.level_id`, menyimpang dari skema lama, dan setiap query yang memfilter level wajib join Jabatan.
- **Tetap denormalisasi, muat Jabatan** (dipilih): pertahankan perilaku + skema lama. Filter tetap pada kolom `profesi.level_id` tersimpan (tanpa join tambahan saat baca). Trade-off: satu kolom denormalisasi yang bisa drift dari `jabatan.level_id` bila sebuah jabatan di-relevel setelah profesi dibuat; satu SELECT ekstra tiap create/update.

## Consequences

- `profesi.level_id` dipertahankan di migrasi V1. Tidak ada DROP kolom.
- Risiko drift: bila `jabatan.level_id` berubah, `profesi.level_id` yang lama **tidak** ikut diperbarui otomatis. Tidak ada mekanisme rekonsiliasi yang ditambahkan di muka (YAGNI) — bila kelak jadi masalah, bisa ditangani via trigger atau job tersendiri.
- Sisi tulis memuat Jabatan via `findById` → `ResourceNotFoundException` (404 menyebut Jabatan) untuk jabatanId tak valid. Organisasi/Grade tetap proxy → FK invalid jadi 409 generik (ADR-0008).
- Field request tetap `organisasiId, jabatanId, gradeId` — **tidak** ada `levelId` di request tulis; level murni turunan server.
- Lingkup: spesifik untuk relasi `Profesi.level` ← `Jabatan.level`. Aggregate master lain dengan FK denormalisasi turunan mengikuti pola yang sama; FK non-turunan tetap ADR-0008.
