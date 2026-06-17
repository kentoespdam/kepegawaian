# Claim Order — Epic `kepegawaian-irt`

> Apply F5/E0: create `MasterBaseEntity` + migrate 17 master entities off `IdsAbstract`.
>
> **Peran:** epic ini sudah didekomposisi oleh manager menjadi 4 child issue yang dapat di-_claim_ independen. Dokumen ini adalah **urutan klaim** beserta alasan dependensinya. Jangan kerjakan `kepegawaian-irt` secara langsung — ia hanya payung desain. Detail kontrak teknis ada di `bd show kepegawaian-irt` (description + notes).

## Peta dependensi

```
irt/1 (9g0)  ──┐
               ├──► irt/4 (d8p)   GolonganWriteIT (bukti akhir)
irt/2 (j4a) ──►irt/3 (c2q) ──┘
```

- **irt/1** dan **irt/2** tidak saling bergantung → boleh dikerjakan **paralel** oleh dua agent.
- **irt/3** butuh `MasterBaseEntity` dari irt/2.
- **irt/4** butuh ketiganya: fix audit (irt/1) + base class (irt/2) + entity termigrasi (irt/3).

## Urutan klaim

| Urutan | Issue | ID | Boleh paralel? | Diblokir oleh | Inti pekerjaan |
|--------|-------|----|----|----|----|
| 1a | irt/1 | `kepegawaian-9g0` | ya (dgn 1b) | — | `AuditAwareImpl.getCurrentAuditor()` null-safe, fallback `"system"` (fix bug #2 NPE master write dev/test) |
| 1b | irt/2 | `kepegawaian-j4a` | ya (dgn 1a) | — | Buat `MasterBaseEntity` `@MappedSuperclass` non-Envers (fondasi) |
| 2 | irt/3 | `kepegawaian-c2q` | tidak | irt/2 | Migrasi 17 entity master `extends IdsAbstract` → `extends MasterBaseEntity`, hapus `@Audited`, pertahankan `@SQLDelete` |
| 3 | irt/4 | `kepegawaian-d8p` | tidak | irt/1 + irt/2 + irt/3 | `GolonganWriteIT` real MariaDB: bukti master non-Envers + audit `system` fallback; ganti `GolonganTest` mock lama |

## Cara klaim & tutup (beads)

```bash
bd ready                 # irt/1 & irt/2 muncul; irt/3 & irt/4 blocked sampai blocker selesai
bd show <id>             # baca kontrak penuh sebelum mulai
bd update <id> --claim   # klaim sebelum menulis kode
bd close <id>            # tutup setelah quality gate hijau
```

Saat irt/2 ditutup, irt/3 otomatis masuk `bd ready`. Saat irt/1+irt/2+irt/3 ketiganya ditutup, irt/4 masuk `bd ready`.

## Catatan per-issue

### irt/1 — `kepegawaian-9g0` (INDEPENDEN, mulai dulu)
`AuditConfig` (`@EnableJpaAuditing` + `auditorProvider`) sudah ter-wire — **bukan** pekerjaan baru. Pekerjaan = bikin `getCurrentAuditor()` null-safe (Option A):
- `auth == null` atau principal bukan `AppwriteUser` → `Optional.of("system")`.
- selain itu → `Optional.of(user.get$id())`.

### irt/2 — `kepegawaian-j4a` (INDEPENDEN, blok irt/3)
`MasterBaseEntity` final: `@MappedSuperclass` + `@EntityListeners(AuditingEntityListener.class)` + `@SQLRestriction("is_deleted = FALSE")`, field `id`/`createdBy`/`createdAt`/`updatedBy`/`updatedAt`/`isDeleted`, equals/hashCode HibernateProxy-aware dari `IdsAbstract`.
**Larangan:** TANPA `@Version`, TANPA `@Audited`/Envers, TANPA `changed_status`, TANPA `@JsonFormat`. `@SQLDelete` tetap per-entity. **Jangan** sentuh/hapus `IdsAbstract` (29 entity non-master masih pakai).

### irt/3 — `kepegawaian-c2q` (butuh irt/2)
17 entity: AlasanBerhenti, AlatKerja, Apd, Golongan, Grade, HariLibur, Jabatan, JenisKeahlian, JenisKitas, JenisPelatihan, JenisSp, JenjangPendidikan, Level, Organisasi, Profesi, RumahDinas, Sanksi.
Per entity: `extends MasterBaseEntity`, hapus `@Audited`, pertahankan `@SQLDelete` (nama tabel literal). Hati-hati tree (Jabatan, Organisasi). GitNexus impact sebelum edit; jangan rename via find/replace.

### irt/4 — `kepegawaian-d8p` (butuh irt/1+irt/2+irt/3)
Ganti `GolonganTest` mock-only (yang "membuktikan nol") dengan integration test nyata terhadap MariaDB eksternal. Repository asli (bukan `@Mock`), tanpa principal palsu (uji fallback `system`), assert tidak ada `golongan_aud`, assert soft-delete + `@SQLRestriction`. Harus **gagal-merah** bila salah satu dari irt/1/2/3 di-revert (anti-masking).
