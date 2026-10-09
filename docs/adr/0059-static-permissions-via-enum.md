# 0059: Static Permissions via Java Enum

- **Status**: Accepted
- **Context**: Sebelumnya permission disimpan di tabel `pref_permission` dan direlasikan ke role secara dinamis. Ini menyebabkan rawan permission invalid, error jika DB tidak sync dengan kode, dan kurang type-safe di sisi Java.
- **Decision**: Menggunakan static Java Enum (`AppPermission`) untuk merepresentasikan seluruh permission. Relasi di Entity Role menggunakan `@ElementCollection` bertipe Enum tersebut. Tabel master `pref_permission` dihapus, namun join table `pref_role_permission` dipertahankan (tanpa FK constraint ke tabel master).
- **Consequences**:
  - Positif: Type-safety terjamin via JPA, tidak ada lagi orphan permission. Endpoint check lebih cepat tanpa query ke tabel master.
  - Negatif: Penambahan permission baru wajib deploy ulang backend (recompile), tidak bisa ditambah on-the-fly via DB. Tapi hal ini sepadan karena logika backend juga butuh update untuk mengenali permission baru.
