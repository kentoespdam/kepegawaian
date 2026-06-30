# Context — Contoh Dialog & Ambiguitas Terflag

Bagian dari [CONTEXT-MAP.md](../../CONTEXT-MAP.md). Baca file ini untuk memahami bagaimana domain expert berbicara tentang konsep ini, dan istilah-istilah yang berpotensi ambigu.

## Example Dialogue

> **Dev:** "Saat membuat **Profesi**, apakah user memilih **Level**?"
> **Domain expert:** "Tidak. **Level** ikut **Jabatan** yang dipilih — kalau Jabatan-nya berubah, Level-nya ikut. User hanya pilih Organisasi, Jabatan, dan Grade."

> **Dev:** "Kalau aku hapus **Profesi** 'Operator IPA', lalu nanti membuatnya lagi dengan Jabatan & Grade yang sama — itu data baru atau yang lama?"
> **Domain expert:** "Yang lama hidup lagi. Tidak ada duplikat — yang terhapus dipulihkan, lalu isinya diperbarui sesuai input baru."

> **Dev:** "Kalau aku **edit** Profesi 'Operator IPB' lalu mengubah Jabatan & Grade-nya supaya persis sama dengan 'Operator IPA' yang sudah ada — boleh?"
> **Domain expert:** "Tidak. Itu akan jadi dua hal yang sama. Edit harus ditolak. Kalau memang mau menghidupkan kombinasi yang pernah ada, buat ulang lewat tambah data — bukan lewat edit."

## Flagged Ambiguities

- **"Position"** dipakai ambigu di kode lama untuk **Profesi** maupun **Jabatan** — keduanya konsep berbeda; gunakan istilah Indonesia (Profesi / Jabatan) untuk membedakan.
