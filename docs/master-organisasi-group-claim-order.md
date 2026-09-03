# Master Organisasi — Field `group` (Grup Hierarki) — Claim Order & Monitoring

Issue **kepegawaian-m04g** — tambah field `group` pada master **Organisasi** untuk grouping data pegawai oleh FE (induk + seluruh sub jadi 1 grup).

> **Mode**: Grilling (Manager) → output issue + checklist ini. Implementasi dilakukan di Coding Mode oleh agent yang meng-claim issue.

**Sebelum klaim**, baca keputusan manajer yang terkunci di `bd show kepegawaian-m04g` dan glossary `docs/context/language-master.md` (`Grup Organisasi`). Keputusan berikut tidak boleh diubah tanpa eskalasi:

1. Nilai `group` = **teks bebas**, ditulis manual per record oleh staf SDM — bukan whitelist, bukan inherit otomatis.
2. **Wajib** (`@NotBlank` create/update, kolom DB NOT NULL).
3. **Exposure opsi 2** — `group` hanya di endpoint master (`OrganisasiQuery` + `OrganisasiListResponse`). `OrganisasiMiniResponse` **TIDAK** disentuh.
4. Filter index: `group` = query param equal (pola `category`), **tanpa** whitelist sort.
5. Kolom DB `org_group` (`group` reserved word MariaDB); JSON/DTO tetap `group`.
6. Uniqueness tetap `nama` + `parent` — `group` bukan bagian kunci.

**Per-step wajib:** `gitnexus_impact(repo:"kepegawaian")` sebelum edit · `detect_changes` sebelum commit · `./gradlew build` hijau sebelum `bd close` · soft-delete dipertahankan · **jangan** rename via find/replace.

---

## Claim Order (1 issue, step berurutan — kerjakan di satu klaim)

- [x] **m04g** · #1: Migration `V37__add_group_to_organisasi.sql` — `ALTER TABLE organisasi ADD COLUMN org_group VARCHAR(64) NOT NULL DEFAULT ''` + backfill `group` dari mapping nama→group di `docs/organization.sql` (record tanpa match tetap `''`)
- [x] **m04g** · #2: `./gradlew jooqCodegen` — regen jOOQ (`Organisasi.ORG_GROUP`, `OrganisasiRecord`)
- [x] **m04g** · #3: Entity `Organisasi` — field `group` + `@Column(name = "org_group")` (JPA path); jangan ubah kunci keunikan
- [x] **m04g** · #4: DTO — `OrganisasiPostRequest.group` `@NotBlank("Group tidak boleh kosong")` (diwarisi Put), `OrganisasiQuery` + `group`, `OrganisasiListResponse` → `(id, nama, group)`, `OrganisasiIndexQuery` + filter `group` (equal)
- [x] **m04g** · #5: Mapper — `OrganisasiMapper.toEntity/updateEntity` set group
- [x] **m04g** · #6: JOOQ — `OrganisasiSelects` tambah `GROUP` ke `ORGANISASI_COLUMNS`; `OrganisasiJooqMapper.toQuery` map group
- [x] **m04g** · #7: Query repo — `pageQuery` filter group di count + data; `listQuery` select `org_group` (fetchInto record baru)
- [x] **m04g** · #8: Tests — `OrganisasiCommandServiceTest` (create/update + `@NotBlank` group), test filter group
- [x] **m04g** · #9: `./gradlew build` zero error + `gitnexus_detect_changes()` scope hanya modul master/organisasi (+ migration + jooq)
- [x] **m04g** · #10: Close issue → commit → `bd dolt push` → `git pull --rebase` → `git push`

## Acceptance (ringkas)

- [x] `group` wajib di create/update (`@NotBlank`), kolom DB NOT NULL
- [x] `GET /master/organisasi?group=...` filter; `list`/detail/parent mengembalikan `group`
- [x] `OrganisasiMiniResponse` tidak berubah — response modul lain (pegawai/cuti/riwayat) identik
- [x] Backfill benar dari `organization.sql` (match nama); migration tidak crash
- [x] `./gradlew build` hijau

## Referensi

- Glossary: `docs/context/language-master.md` — `Grup Organisasi` (vs `Category Organisasi`)
- Sumber data lama: `docs/organization.sql` — kolom `group` di `smartoffice.organization`
- Seed saat ini: `src/main/resources/db/migration/V3__seed_organisasi.sql`
- Pola filter existing: `OrganisasiIndexQuery.category` → `SpecificationBuilder.addEqual` + join `pageQuery` count/data
- ADR-0002 (Flyway source of truth) · ADR-0015 (jOOQ generated committed, regen manual) · ADR-0005 (revive-on-create, kunci keunikan nama+parent)
- JPA write / JOOQ read: ADR-0001