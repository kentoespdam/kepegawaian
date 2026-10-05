# Checklist Implementasi Claim Order Pattern Laporan

- [ ] Menerima request user (DTO filter parameter laporan) di Controller.
- [ ] Validasi Otorisasi menggunakan `@PreAuthorize` (contoh: `hasAuthority('PEGAWAI:READ')`).
- [ ] Buat record `ClaimOrder` di database dengan status `PENDING`.
- [ ] Kembalikan *Claim Order ID* (Ticket) ke client secara instan (HTTP 202 Accepted).
- [ ] Lempar event eksekusi secara Asynchronous (Background Thread/Worker):
  - [ ] Ubah status `ClaimOrder` menjadi `PROCESSING`.
  - [ ] Tarik data melalui jOOQ Query Service (Read-Only) yang telah ditranslasi dari Raw SQL.
  - [ ] Load template `.xlsx` dari `src/main/resources/templates/laporan/`.
  - [ ] Isi data menggunakan Jxls / Apache POI.
  - [ ] Simpan file Excel biner ke Local File System via `FileUploadUtilImpl`.
  - [ ] Update record `ClaimOrder` di DB dengan path lokasi fisik file dan ubah status menjadi `COMPLETED` (atau `FAILED` bila error).
- [ ] Expose endpoint *Download* (`GET /laporan/download/{ticketId}`) yang men-*stream* file Excel ke browser (jika status `COMPLETED`).
- [ ] Daftarkan `@Scheduled` cron job berjalan tiap tengah malam untuk membersihkan fisik file laporan sementara (> 24 jam) dari Local Storage.
