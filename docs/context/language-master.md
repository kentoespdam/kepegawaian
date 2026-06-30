# Context — Modul Master (Data Referensi)

Bagian dari [CONTEXT-MAP.md](../../CONTEXT-MAP.md). Baca file ini saat mengerjakan modul `master/` (Profesi, Jabatan, Organisasi, Grade, Level, APD, Alat Kerja).

## Glossary

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

## Aturan Bisnis Penting

- **Level** sebuah **Profesi** **diturunkan dari Jabatan-nya** (`profesi.level = jabatan.level`) — tidak diinput terpisah. Form pembuatan Profesi tidak meminta `levelId`.
- Keunikan **Profesi** ditentukan oleh kombinasi `nama` + **Jabatan** + **Grade** (dasar cek duplikat).
- Membuat **Profesi** dengan kombinasi yang dulu pernah dihapus akan **menghidupkan kembali** record lama itu (bukan membuat data baru).
- **Mengubah** sebuah **Profesi** agar kombinasinya sama dengan Profesi lain — baik yang aktif maupun yang sudah diarsip — **ditolak**. Menghidupkan kembali kombinasi arsip hanya lewat pembuatan ulang (create), bukan lewat edit.
- Keunikan **Organisasi** ditentukan oleh kombinasi `nama` + **parent** (dasar cek duplikat). Kode dan level TIDAK masuk kunci — `kode` adalah label tampilan dan `level` adalah metadata struktur, bukan identitas.
- Membuat **Organisasi** dengan kombinasi nama+parent yang dulu pernah dihapus akan **menghidupkan kembali** record lama itu.
- **Mengubah** sebuah **Organisasi** agar nama+parent-nya sama dengan Organisasi lain — baik aktif maupun diarsip — **ditolak**.
