# Service sebagai kelas konkret, tanpa interface + Impl

Rewrite mendefinisikan service sebagai **kelas konkret** ber-`@Service`, tanpa pasangan interface + `*Impl`. Kode lama memakai pola interface + `Impl` di seluruh layer service (55 interface, 53 `Impl`, selalu 1:1) — tetapi tidak ada satu pun interface yang punya implementasi kedua. Polimorfisme itu tidak pernah ada.

```java
@Service
@RequiredArgsConstructor
public class ProfesiCommandService { ... }   // jalur tulis (JPA)

@Service
@RequiredArgsConstructor
public class ProfesiQueryService { ... }      // jalur baca (JOOQ)
```

Controller meng-autowire tipe konkret langsung. Tidak ada `ProfesiService` interface, tidak ada `ProfesiServiceImpl`.

Best practice Spring (diverifikasi via context7, dok resmi Spring Boot) mendukung ini: rekomendasi adalah **constructor injection + component scan**, dan "provide as much type information as possible... bean's return type should be the concrete class and not the interface". Tidak ada anjuran Spring untuk membuat interface pada bean yang hanya punya satu implementasi.

## Considered Options

- **Interface + `Impl`** (ditolak): sesuai kode lama, diff mental paling kecil, sebagian shop mewajibkan interface untuk semua bean. Tetapi menggandakan jumlah file untuk polimorfisme yang tidak pernah dipakai; dengan CQRS (Command/Query terpisah, ADR-0001) jadi 4 file (2 interface + 2 impl) di mana 2 kelas konkret sudah cukup. Melanggar KISS.
- **Kelas konkret** (dipilih): satu file per service, tanpa indireksi. Mockito tetap bisa mem-mock kelas konkret, jadi testability tidak hilang. Sesuai KISS — tujuan eksplisit rewrite.

## Consequences

- Menyimpang dari konvensi project lama: siapa pun yang porting kode harus menahan refleks menambah `Impl`.
- Bila suatu saat benar-benar butuh implementasi kedua (mis. swap teknologi), interface bisa diekstrak saat itu juga (refactor IDE sepele). Tidak diantisipasi di muka — YAGNI.
- Berlaku untuk semua service di semua modul rewrite, bukan hanya master.
