# Envers dibatasi ke modul penggajian dan kepegawaian

Full revision history (Spring Data Envers, `@Audited`) hanya dipakai di modul **penggajian** dan **kepegawaian**. Modul master cukup kolom audit (`created/updated by/at`) + soft-delete, tanpa Envers.

Alasan: data master adalah referensi yang jarang berubah (Profesi, Level, Golongan, Grade, Organisasi, Jabatan) — kolom `updated_by/at` + soft-delete sudah menjawab "siapa terakhir mengubah". Full history hanya bernilai pada data dengan kebutuhan jejak perubahan kuat: gaji, SK, kontrak, terminasi. Project lama memasang Envers di 43 entity secara seragam (over, tidak tepat sasaran).

## Consequences

- Modul master: tidak ada tabel `*_aud`. Schema lebih ramping.
- Alasan utama ADR-0001 (JPA-on-write demi Envers) tidak berlaku untuk master; di master JPA dipertahankan karena soft-delete + optimistic lock + kolom audit. Lihat catatan di ADR-0001.
- Saat Envers dipakai (penggajian/kepegawaian), `AuditRevisionListener` **wajib diaktifkan** — di project lama file itu di-comment penuh sehingga Envers tak mencatat *siapa* pelaku tiap revisi. Bug ini tidak boleh terbawa ke rewrite.
