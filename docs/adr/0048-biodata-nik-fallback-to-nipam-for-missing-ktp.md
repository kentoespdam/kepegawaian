# Fallback NIK Biodata ke NIPAM untuk Profil Tanpa KTP Valid

Dalam migrasi data dari `smartoffice.emp_profile` ke `kepegawaian_dev_new.biodata`, profil legacy yang tidak memiliki NIK KTP valid akan menggunakan **NIPAM pegawai (`employee.emp_code`) sebagai NIK sementara**, disertai pencatatan ke berkas audit.

## Konteks

Pada aplikasi legacy `smartoffice`, tabel `employee` berelasi ke `emp_profile` menggunakan integer ID (`emp_profile_id`). Di aplikasi baru, entitas `biodata` menggunakan `nik` sebagai Primary Key dan seluruh tabel personal anak (`pendidikan`, `profil_keluarga`, `pelatihan`, `keahlian`, `kartu_identitas`) memiliki foreign key ke `biodata.nik`. Pada data legacy, ditemukan sebagian profil yang tidak memiliki data NIK KTP (kosong, strip, atau null). Jika record ini dilewati (skip), seluruh riwayat pendidikan dan keluarga milik pegawai tersebut akan hilang.

## Considered Options

- **Skip Profil Tanpa NIK KTP** (ditolak): Menyebabkan kehilangan data keluarga, pendidikan, dan pelatihan pegawai yang bersangkutan.
- **Generate 16-Digit Acak** (ditolak): Berpotensi membingungkan tim SDM karena angka acak terlihat seperti NIK asli.
- **Fallback ke NIPAM Pegawai + Log Audit** (dipilih): Menggunakan NIPAM (`emp_code`) sebagai NIK sementara pada `biodata.nik` jika NIK KTP tidak ditemukan baik di `emp_profile.emp_identity_number` maupun `emp_card`.

## Consequences

- Seluruh data profil dan relasi anak dapat termigrasi 100% tanpa adanya data orphan atau kehilangan riwayat.
- Runner migrasi mengekspor berkas `audit_unresolved_nik.csv` berisi daftar NIPAM dan nama pegawai yang menggunakan NIK sementara agar staf SDM dapat memperbaruinya di aplikasi baru.
- Tabel `migration_id_map` mencatat pemetaan `(legacy_table='emp_profile', legacy_id=emp_profile_id) -> (new_table='biodata', new_id=nik)` untuk memudahkan lookup oleh modul anak.
