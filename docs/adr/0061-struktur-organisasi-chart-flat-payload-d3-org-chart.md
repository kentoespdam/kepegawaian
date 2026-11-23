# 0061: Struktur Organisasi Chart Flat Payload untuk d3-org-chart

- **Status**: Accepted
- **Context**: 
  Frontend modern (`kepegawaian-fe`) membutuhkan visualisasi pohon struktur organisasi perusahaan interaktif (fitur pencarian pejabat/jabatan, zoom & pan, minimap navigator, dan export dokumen) yang menggantikan bagan legacy GoJS di SmartOffice.
  Berdasarkan riset, library open-source berlisensi MIT terbaik yang dipilih adalah `d3-org-chart`. Library ini mensyaratkan data masukan berupa **Flat Array** di mana setiap elemen memiliki relasi `id` dan `parentId` (root node memiliki `parentId = null`).
  Sebelumnya, endpoint `GET /laporan/kepegawaian/so` mengembalikan single nested object `{ hierarchy: { ... } }` yang kurang optimal dan sulit diparsing langsung oleh library visualisasi modern berbasis flat relational array.

- **Decision**:
  1. **Dual Format Support**: Endpoint `GET /laporan/kepegawaian/so` secara default mengembalikan **Flat Array** (`CustomResult.list(...)`), dan menyediakan opsi query parameter `?format=tree` untuk klien yang membutuhkan struktur hierarki berakar (nested tree dengan array `children`).
  2. **Standard Field Naming**: Mengubah field legacy `key` dan `boss` menjadi `id` dan `parentId` (bernilai `null` untuk root/Dewan Pengawas). Menggunakan penamaan field deskriptif: `kodeJabatan`, `namaJabatan`, `level`, `namaPegawai`, `nipam`, `namaOrganisasi`, `fotoProfil`, dan `isLowong`.
  3. **Depth Filter**: Menyediakan parameter opsional `?maxLevel=N` dengan nilai bawaan `6` (menampilkan posisi struktural dari Dewan Pengawas & Direksi hingga tingkat Supervisor, mengecualikan Staf pelaksana reguler level 7).
  4. **Vacancy Handling**: Jabatan struktural yang belum memiliki pejabat aktif (`isLowong = true`) tetap dimunculkan di dalam bagan organisasi dengan `namaPegawai = null` dan `nipam = null`.
  5. **Security & Authorization**: Mempertahankan perlindungan `@PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ') or hasAuthority('PEGAWAI:READ')")`.

- **Consequences**:
  - Positif: Kompatibel langsung 100% dengan `d3-org-chart` di frontend tanpa transformasi berat di sisi browser. Mendukung rendering status lowong dan informasi unit kerja organisasi secara komprehensif.
  - Negatif / Mitigasi: Konsumer internal lama yang mengharapkan format `{ data: { hierarchy: { key, boss, ... } } }` perlu menggunakan parameter `?format=tree` atau memperbarui parser payload ke format baru.
