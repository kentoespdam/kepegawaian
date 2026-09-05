# Sinkronisasi Terautomasi Akun Appwrite Auth dan Pengelolaan Lifecycle Pengguna

Untuk memastikan seluruh pegawai aktif dapat langsung mengakses aplikasi baru pasca-migrasi data tanpa alur registrasi manual serta menegakkan keamanan akses eks-pegawai, runner migrasi mengadopsi modul sinkronisasi terautomasi ke **Appwrite Auth** yang mem-provision akun pegawai aktif dan menonaktifkan akun pegawai pensiun/berhenti secara terpusat dan idempoten.

## Konteks

Pada sistem baru `kepegawaian_dev_new`, tidak tersedia tabel pengguna lokal (`sys_user`); sistem murni menggunakan Appwrite Auth (endpoint: `http://192.168.230.254:82/v1`) sebagai identitas terpusat. Frontend login langsung ke Appwrite untuk mendapatkan JWT, lalu backend memvalidasi token dan melakukan *Permission Inflation* berbasis role di MariaDB ([ADR-0037](0037-rbac-permission-per-role-didb-mariadb.md)).

Sistem kepegawaian baru tidak memiliki alur registrasi mandiri (*self-registration*) bagi pegawai demi menjaga kendali tata kelola SDM terpusat. Jika pegawai aktif hasil migrasi belum terdaftar di Appwrite, mereka tidak dapat melakukan login sama sekali ke dalam aplikasi. Selain itu, sesuai kebijakan *lifecycle user* ([ADR-0039](0039-rbac-user-lifecycle-no-hard-delete.md)), akun pegawai yang telah pensiun atau berhenti (`status_kerja = 3`) harus dinonaktifkan/diblokir di Appwrite demi menegakkan keamanan akses eks-pegawai tanpa menghapus jejak audit.

## Considered Options

- **Provisioning Akun Manual via Konsol Appwrite** (ditolak): Tim administrator mendaftarkan akun pegawai satu per satu melalui UI konsol Appwrite. Pendekatan ini rentan *human error*, membutuhkan waktu lama untuk ratusan pegawai, dan rawan melewatkan status non-aktif bagi akun pegawai yang telah pensiun atau berhenti.
- **Just-In-Time (JIT) Auto-registration di Backend** (ditolak): Mencoba membuatkan akun saat percobaan login pertama di backend Java. Secara arsitektural pilihan ini tidak dapat diimplementasikan karena frontend melakukan autentikasi langsung ke Appwrite Server untuk memperoleh JWT sebelum melakukan request ke backend; ketiadaan akun di Appwrite menggagalkan login sejak tahap pertama di frontend.
- **Sinkronisasi Terautomasi Idempoten via Runner Migrasi** (dipilih):
  1. Runner membaca data pegawai dari database target pasca-migrasi data kepegawaian.
  2. Melakukan audit dan verifikasi akun ke Appwrite Server REST API (`/v1/users`).
  3. Untuk pegawai aktif (`status_kerja = 1` atau `2`) yang belum memiliki akun di Appwrite: secara otomatis membuatkan akun baru dengan ID setara `pegawai.id`, email `<nipam>@perumdamts.com`, nama lengkap, default password `"tirtasatria"`, dan preferences `prefs = {"roles": ["USER"]}`.
  4. Untuk pegawai yang berstatus pensiun atau berhenti (`status_kerja = 3`): jika akunnya ada di Appwrite, status akun diubah menjadi non-aktif / blocked (`status: false`) untuk menegakkan *lifecycle security*.
  5. Akun pegawai aktif yang sudah terdaftar tidak di-reset kredensial atau passwordnya (idempoten dan menjaga keamanan password personal yang sudah diset pengguna).

## Consequences

- Seluruh pegawai aktif hasil migrasi langsung dapat login ke aplikasi baru pasca-migrasi tanpa hambatan atau intervensi manual dari tim IT.
- Kebijakan *lifecycle security* ditegakkan secara konsisten: akun eks-pegawai (pensiun atau keluar) otomatis terblokir sehingga akses login dicabut.
- Proses sinkronisasi bersifat idempoten dan aman dieksekusi berulang kali, baik sebagai sub-command mandiri (`python run.py sync-auth`) maupun sebagai tahapan pipeline migrasi utama.
- Runner migrasi membutuhkan akses konfigurasi kredensial Appwrite Server API Key (`APPWRITE_ENDPOINT`, `APPWRITE_PROJECT_ID`, `APPWRITE_API_KEY`) dengan scope otorisasi manajemen pengguna (`users.read`, `users.write`).
