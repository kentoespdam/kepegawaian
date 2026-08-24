# Organisasi — Adopsi Pattern Response Publication — Claim Order & Monitoring

Epic **kepegawaian-dcz** — Adopsi pattern response/DTO modul Publication (repo `kentoespdam/mail-migration`) ke modul master **Organisasi**.

Urutan klaim di bawah mengikuti **dependency**, bukan nomor issue. Dua child issue (#6 & #7) **menyentuh file berbeda dan tidak saling blok** → boleh diklaim paralel. `bd ready` selalu jadi sumber kebenaran issue yang sudah unblocked.

**Sebelum klaim apa pun**, baca keputusan manajer yang dikunci di `bd show kepegawaian-dcz`. Tiga keputusan tidak boleh diubah tanpa eskalasi:
1. Tipe response = pakai `OrganisasiQuery` langsung — **tidak** ada DTO baru, **tidak** MapStruct, **tidak** sqids.
2. Endpoint mutasi cukup kembalikan `{status, id}` — **tidak** re-read, **tidak** kembalikan entity.
3. `PagedRequest` + `SortParam` ditulis baru **di dalam modul master** (bukan commons global).

**Per-issue wajib:** `gitnexus_impact(repo:"kepegawaian")` sebelum edit · `detect_changes` sebelum commit · `./gradlew test` hijau · soft-delete dipertahankan · **jangan** rename via find/replace (pakai `gitnexus_rename`).

---

## WAVE 0 — Epic (gerbang, tidak dikerjakan langsung)

- [x] **dcz** · #5 (EPIC): adopsi pattern response Publication ke modul master Organisasi · `P2` · deps: —

> Epic ini hanya kontainer keputusan + grounding. Tidak ada koding di sini. Tutup setelah #6 & #7 selesai.

## WAVE 1 — Eksekusi paralel (2 issue, tidak saling blok)

- [x] **5f8** · #6: `PagedRequest` + `SortParam` whitelist di modul master + migrasi switch inline `OrganisasiQueryRepository` (~baris 26-33) → `allowedSorts()` deklaratif · `P2` · deps: dcz
- [x] **smp** · #7: write-flow controller — `OrganisasiController` POST/PUT/DELETE kembalikan `{status, id}` saja (swap `entity` → `entity.getId()` ke `SavedStatus.build`) · `P2` · deps: dcz

> #6 menyentuh `dto/master/organisasi` + `OrganisasiQueryRepository`. #7 menyentuh `OrganisasiController` saja. Tidak ada file bertabrakan → aman paralel.

---

## Dependency map (ringkas)

```
dcz(#5 epic) ──┬── 5f8(#6 paging/sort)
               └── smp(#7 write-flow)
```

## Acceptance ringkas per issue

**#6 (5f8)**
- [x] `PagedRequest` & `SortParam` ada di dalam modul master (bukan `dto/commons` global)
- [x] `OrganisasiIndexQuery` tidak lagi `extends CommonPageRequest`
- [x] Sort `kode/nama/levelOrg/shortName/category` & default identik perilaku lama; `sortBy` tak dikenal → default `ID` (tidak error, tidak kolom mentah)
- [x] Nol dependency build baru
- [x] Blast radius dilaporkan sebelum edit; `detect_changes` bersih dari modul lain

**#7 (smp)**
- [x] POST & PUT mengembalikan body `{status, id}` (Long), bukan entity penuh
- [x] Tidak ada kebocoran relasi/lazy entity pada response mutasi
- [x] `OrganisasiCommandService` tidak berubah (idealnya), atau perubahan dijustifikasi impact
- [x] `detect_changes` hanya menunjukkan `OrganisasiController`
- [x] DELETE tetap soft-delete

## Cara update checklist

- Klaim: `bd update kepegawaian-<id> --claim`
- Selesai: `bd close kepegawaian-<id>` → centang `[x]` di sini
- Cek yang siap dikerjakan: `bd ready`
- Status keseluruhan: `bd list --status=open | grep -E 'dcz|5f8|smp'`

## REF

- Pattern sumber: `PublicationController` / `PageRequest` / `SortParam` (mail-migration, branch CQRS)
- ADR-0005 (revive-on-create) · kepegawaian-jow (kunci keunikan nama+parent) · kepegawaian-33s (carcass-finder native)
