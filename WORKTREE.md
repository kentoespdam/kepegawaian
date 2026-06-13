# Worktree Setup — Rewrite & Legacy Reference

Proyek ini sedang dalam **rewrite** (Spring Boot 4 + CQRS). Supaya kode lama tidak ikut
ter-compile (beda versi Spring Boot) tapi tetap bisa dibaca sebagai referensi spec saat
grilling tiap modul, kita memakai **git worktree**.

## Layout

| Lokasi | Ref Git | Peran |
|--------|---------|-------|
| `/mnt/DATA/idea/kepegawaian` | branch `rewrite/master-cqrs` | **Folder kerja utama.** Tempat rewrite bersih. `src` lama dihapus/ditulis ulang di sini. |
| `/mnt/DATA/idea/kepegawaian-legacy` | tag `legacy-snapshot` (detached HEAD) | **Read-only.** Kode lama 671 file Java utuh — acuan spec saat grilling modul cuti/penggajian/dll. |

Tag `legacy-snapshot` menunjuk commit `a3ff134` ("plan: master-module rewrite"). `src` di tag
ini identik dengan kondisi pre-rewrite, jadi aman dipakai sebagai referensi.

## Aturan

- **Jangan compile/build** dari folder utama dengan harapan kode lama ikut — kode lama hanya ada
  di `../kepegawaian-legacy`, terpisah, sengaja tidak di build path folder utama.
- **Worktree legacy bersifat read-only.** Jangan commit di sana (detached HEAD). Ia hanya untuk dibaca.
- Saat grilling/rewrite modul X, buka implementasi lama di
  `../kepegawaian-legacy/src/main/java/id/perumdamts/kepegawaian/<modul>/` sebagai acuan.
- Kode lama aman permanen lewat tag `legacy-snapshot` meski worktree dihapus.

## Perintah

```bash
# Lihat daftar worktree
git worktree list

# Buat ulang worktree legacy kalau terhapus
git worktree add ../kepegawaian-legacy legacy-snapshot

# Hapus worktree legacy kalau sudah tak dibutuhkan
git worktree remove ../kepegawaian-legacy
```

## Catatan

Folder lain di sebelah (`kepegawaian_old`, `kepegawaian-mar`, `kepegawaian_new_gradle`) adalah
**kopian manual lama, bukan worktree**. Abaikan; sumber kebenaran kode lama adalah
`kepegawaian-legacy` (tag `legacy-snapshot`).
