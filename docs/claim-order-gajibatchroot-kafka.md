# Claim Order — GajiBatchRootServiceImpl (Kafka)

Empat beads issue hasil analisis `GajiBatchRootServiceImpl.java` (scope: *kafkaTemplate error*).

**Penting:** keempatnya menyentuh file yang sama (`GajiBatchRootServiceImpl.java`) — kerjakan **serial**, jangan paralel, agar tidak konflik. Urutan di bawah disusun berdasarkan ketergantungan logis: konfirmasi pipeline → benahi struktur transaksi → tangani hasil send → polish.

| Urut | ID | Prio | Judul singkat | Alasan urutan |
|------|----|------|---------------|---------------|
| 1 | `kepegawaian-1kv` | P2 | `KafkaTemplate<String,Object>` vs `StringSerializer` + konsumen ter-comment | **Kerjakan dulu.** Konfirmasi apakah consumer benar-benar ada (mungkin di service penggajian eksternal). Jika pipeline mati/no-op, prioritas issue lain bisa berubah. Sekalian persempit tipe generic. |
| 2 | `kepegawaian-kdo` | P2 | Dual-write DB + Kafka tanpa batas transaksi | Tetapkan struktur transaksi dulu: bungkus `save()` dengan `@Transactional`, hapus `@Transactional` salah tempat di `findAll()`. Jadi fondasi untuk #3. |
| 3 | `kepegawaian-48z` | P1 | Hasil `kafkaTemplate.send()` diabaikan — kegagalan kirim senyap | **Bug paling kritis**, tapi solusi idealnya (`afterCommit` hook / outbox) bergantung pada batas transaksi dari #2. Karena itu dikerjakan **setelah** kdo. |
| 4 | `kepegawaian-0wy` | P3 | `catch (Exception)` menelan stack trace + catatan anotasi | Polish terakhir: tambah logging, sentralisasi exception handling. Sebagian (`@Transactional` di `findAll()`) sudah beres di #2 — sinkronkan. |

## Catatan ketergantungan

```
kepegawaian-1kv  (konfirmasi pipeline + tipe)
      │
      ▼
kepegawaian-kdo  (batas transaksi: @Transactional save, lepas findAll)
      │
      ▼
kepegawaian-48z  (tangani send() future di afterCommit hook)
      │
      ▼
kepegawaian-0wy  (logging + cleanup; findAll sudah diurus kdo)
```

- `48z` ⟶ butuh `kdo`: callback `whenComplete` paling andal dipasang di `afterCommit` setelah batas transaksi ada.
- `0wy` ⟶ tumpang tindih dengan `kdo` pada poin `@Transactional findAll()`. Pastikan tidak dikerjakan dua kali — kdo yang menghapusnya, 0wy cukup verifikasi.
- `1kv` ⟶ independen secara kode, tapi temuannya (consumer ada/tidak) bisa mengubah keputusan desain di kdo/48z, jadi diselesaikan paling awal.

## Perintah claim

```bash
bd update kepegawaian-1kv --claim   # 1
bd update kepegawaian-kdo --claim   # 2
bd update kepegawaian-48z --claim   # 3
bd update kepegawaian-0wy --claim   # 4
```
