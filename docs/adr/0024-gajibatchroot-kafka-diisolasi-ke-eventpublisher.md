# Publikasi Kafka GajiBatchRoot diisolasi ke `GajiBatchRootEventPublisher`, dipublish after-commit

> **Status:** accepted — mengikat pemecahan 4-file `GajiBatchRoot` pada rewrite CQRS penggajian (`kepegawaian-awf.12`).

Saat `GajiBatchRootServiceImpl` dipecah untuk CQRS (read=JOOQ, write=JPA) dan agar tiap file ≤ 120 baris, tanggung jawab publikasi Kafka **dikeluarkan** dari `CommandService`/`WorkflowCommandService` ke satu kelas terdedikasi `GajiBatchRootEventPublisher`. `KafkaTemplate` dan `@Value("${spring.kafka.topic}") PENGGAJIAN_TOPIC` **hanya** hidup di kelas ini; Command/Workflow menginject publisher, bukan `KafkaTemplate`. Publikasi tetap memakai pola after-commit: `TransactionSynchronizationManager.registerSynchronization(...)` dengan `send()` di `afterCommit()` (fire-and-forget, hanya log on failure) — bukan `send()` inline di dalam transaksi.

## Considered Options

- **`send()` inline di dalam method tulis `@Transactional`** (ditolak): pesan bisa terkirim lalu transaksi DB rollback → consumer memproses batch yang tak pernah ter-commit. Erat kopling `KafkaTemplate` ke logika tulis.
- **After-commit di dalam CommandService/Workflow** (ditolak): benar secara timing, tapi `KafkaTemplate` + topic + boilerplate `TransactionSynchronization` menggelembungkan file tulis melewati 120 baris dan mencampur concern messaging dengan logika domain.
- **Isolasi ke `GajiBatchRootEventPublisher`, publish after-commit** (dipilih): kontrak topic lintas-service terkumpul di satu tempat, file tulis tetap ramping & fokus domain, dan urutan commit-lalu-publish dipertahankan verbatim dari kode lama.

## Consequences

- **Publikasi tidak transaksional dengan DB.** After-commit fire-and-forget berarti bila `send()` gagal **setelah** commit, batch ter-commit tanpa event terkirim — hanya tercatat di log, tanpa retry/outbox. Diterima untuk scope ini; bila drift terbukti mengganggu, langkah lanjut = outbox pattern (di-backlog, bukan sekarang). Mode kegagalan sebaliknya (event terkirim untuk batch yang rollback) sudah dihilangkan oleh after-commit.
- **`GajiBatchRootEventPublisher` menjadi titik-tunggal kontrak topic** — perubahan nama topic / bentuk payload cukup di satu file, bukan tersebar di jalur tulis.
- **`KafkaTemplate` null-safe dipertahankan** (profil dev tanpa broker): guard `kafkaTemplate == null → log.warn & skip` dipindah apa adanya ke publisher, sehingga Command/Workflow tak perlu tahu Kafka boleh absen.
- **Relasi:** melengkapi pola tulis penggajian; sejalan dengan [ADR-0021](0021-pegawai-saga-atomik-dengan-sistem-eksternal.md) yang menaruh efek sistem-eksternal **sesudah** tulis DB — di sini efek eksternal (publish) bahkan digeser sesudah **commit**.
