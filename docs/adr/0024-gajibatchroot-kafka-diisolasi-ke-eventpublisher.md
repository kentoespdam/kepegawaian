# Publikasi Kafka GajiBatchRoot diisolasi ke `GajiBatchRootEventPublisher`, dipublish after-commit

> **Status:** superseded (2026-09-04) — transport Kafka digantikan **ApplicationEvent in-process** (keputusan desain #1, [docs/penggajian-proses-gaji-claim-order.md](../penggajian-proses-gaji-claim-order.md), `kepegawaian-8seb`). Engine proses gaji pindah ke dalam service; tidak ada lagi broker eksternal.

Saat `GajiBatchRootServiceImpl` dipecah untuk CQRS (read=JOOQ, write=JPA) dan agar tiap file ≤ 120 baris, tanggung jawab publikasi Kafka **dikeluarkan** dari `CommandService`/`WorkflowCommandService` ke satu kelas terdedikasi `GajiBatchRootEventPublisher`. `KafkaTemplate` dan `@Value("${spring.kafka.topic}") PENGGAJIAN_TOPIC` **hanya** hidup di kelas ini; Command/Workflow menginject publisher, bukan `KafkaTemplate`. Publikasi tetap memakai pola after-commit: `TransactionSynchronizationManager.registerSynchronization(...)` dengan `send()` di `afterCommit()` (fire-and-forget, hanya log on failure) — bukan `send()` inline di dalam transaksi.

## Superseded — apa yang berubah dan yang bertahan

**Berubah (dieksekusi Wave 2, commit `51a5eb6a`):**

- `GajiBatchRootEventPublisher` tidak lagi menyentuh Kafka — inject `ApplicationEventPublisher`, publish `GajiBatchRootProcessEvent`.
- After-commit tidak lagi via `TransactionSynchronizationManager` manual — dijamin `GajiBatchRootEventListener` dengan `@TransactionalEventListener(phase = AFTER_COMMIT)` + `@Async("gajiProsesExecutor")` (virtual threads).
- Konsumen engine: dulu external service via Kafka (consumer mati di rewrite) → kini in-process `GajiBatchProsesCommandService.prosesGaji()`.
- `KafkaConfig` (bean `NewTopic`), dep `spring-boot-kafka`, env `KAFKA_*`, blok `spring.kafka` dihapus (commit `ed132781`).

**Bertahan (masih mengikat):**

- Isolasi ke `GajiBatchRootEventPublisher` — Command/Workflow tetap inject publisher, bukan event bus.
- Publikasi tetap terjadi *setelah* commit (fire-and-forget, kegagalan hanya log) — sekarang via phase AFTER_COMMIT listener, bukan manual synchronization.

**Bekas mode kegagalan ADR ini** (event hilang bila commit sukses tapi publish gagal, tanpa retry/outbox) **tetap berlaku**: event in-process nyaris tak bisa "hilang", tapi listener async bisa gagal — ditangani status FAILED + startup recovery (Wave 8).

## Considered Options

- **`send()` inline di dalam method tulis `@Transactional`** (ditolak): pesan bisa terkirim lalu transaksi DB rollback → consumer memproses batch yang tak pernah ter-commit. Erat kopling `KafkaTemplate` ke logika tulis.
- **After-commit di dalam CommandService/Workflow** (ditolak): benar secara timing, tapi `KafkaTemplate` + topic + boilerplate `TransactionSynchronization` menggelembungkan file tulis melewati 120 baris dan mencampur concern messaging dengan logika domain.
- **Isolasi ke `GajiBatchRootEventPublisher`, publish after-commit** (dipilih): kontrak topic lintas-service terkumpul di satu tempat, file tulis tetap ramping & fokus domain, dan urutan commit-lalu-publish dipertahankan verbatim dari kode lama.

## Consequences

- **Publikasi tidak transaksional dengan DB.** After-commit fire-and-forget berarti bila `send()` gagal **setelah** commit, batch ter-commit tanpa event terkirim — hanya tercatat di log, tanpa retry/outbox. Diterima untuk scope ini; bila drift terbukti mengganggu, langkah lanjut = outbox pattern (di-backlog, bukan sekarang). Mode kegagalan sebaliknya (event terkirim untuk batch yang rollback) sudah dihilangkan oleh after-commit.
- **`GajiBatchRootEventPublisher` menjadi titik-tunggal kontrak topic** — perubahan nama topic / bentuk payload cukup di satu file, bukan tersebar di jalur tulis.
- **`KafkaTemplate` null-safe dipertahankan** (profil dev tanpa broker): guard `kafkaTemplate == null → log.warn & skip` dipindah apa adanya ke publisher, sehingga Command/Workflow tak perlu tahu Kafka boleh absen.
- **Relasi:** melengkapi pola tulis penggajian; sejalan dengan [ADR-0021](0021-pegawai-saga-atomik-dengan-sistem-eksternal.md) yang menaruh efek sistem-eksternal **sesudah** tulis DB — di sini efek eksternal (publish) bahkan digeser sesudah **commit**.
