# Kepegawaian — Master Context

Domain bahasa untuk sistem manajemen kepegawaian PERUMDAMTS.

> **Rewrite in progress (worktree).** Folder utama (`rewrite/master-cqrs`) untuk kode baru; kode lama ada read-only di `../kepegawaian-legacy` (tag `legacy-snapshot`) sebagai referensi spec. Detail: [WORKTREE.md](WORKTREE.md).

> **Agent guidance:** Canonical ops guidance is in [CLAUDE.md](./CLAUDE.md) — includes GitNexus repo `kepegawaian`, build/run, architecture, issue tracking, and skills.

## Lazy Read — Jangan Baca Semua Sekaligus

File ini hanya entry point. **Baca [`CONTEXT-MAP.md`](./CONTEXT-MAP.md)** untuk menemukan sub-context yang relevan dengan tugasmu, lalu baca hanya file tersebut.

Domain context dipecah per modul/topik di `docs/context/`:

| Sub-context | Topik |
|-------------|-------|
| [`language-master.md`](docs/context/language-master.md) | Profesi, Jabatan, Organisasi, Grade, Level |
| [`decisions-master.md`](docs/context/decisions-master.md) | Keputusan CQRS cleanup modul master (mapper, fetchInto, enum, FK) |
| [`language-pegawai.md`](docs/context/language-pegawai.md) | Pegawai, NIPAM, SK, Ringkasan, JSON baca |
| [`language-profil.md`](docs/context/language-profil.md) | Profil, Pengajuan Perubahan, changedStatus |
| [`language-cuti.md`](docs/context/language-cuti.md) | Cuti, Approval Chain, Kuota Cuti, PIC |
| [`language-penggajian.md`](docs/context/language-penggajian.md) | Penggajian, Dasar Gaji, Batch Gaji, Status Proses, Potongan Tambahan |
| [`language-security.md`](docs/context/language-security.md) | Lingkungan, Dev User, Appwrite JWT, Role |
| [`relationships.md`](docs/context/relationships.md) | Relasi & dependency lintas-modul |
| [`decisions-pegawai.md`](docs/context/decisions-pegawai.md) | Keputusan rewrite pegawai & kepegawaian |
| [`decisions-cuti.md`](docs/context/decisions-cuti.md) | Keputusan rewrite cuti |
| [`examples-and-flags.md`](docs/context/examples-and-flags.md) | Contoh dialog & ambiguitas |
