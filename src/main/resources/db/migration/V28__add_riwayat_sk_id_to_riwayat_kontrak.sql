-- ADR-0034: nomor SK boleh terpakai ulang di riwayat_sk. RiwayatKontrak kini
-- diikat ke baris Riwayat SK spesifik (pola RiwayatMutasi), bukan cocokkan nomor SK.
ALTER TABLE `riwayat_kontrak`
    ADD COLUMN `riwayat_sk_id` bigint(20) NULL AFTER `pegawai_id`;

-- Backfill kontrak lama: pasangkan ke riwayat_sk via (pegawai_id, nomor_kontrak = nomor_sk)
-- DAN tanggal_sk sama. Tanggal_sk wajib disertakan: sebelum ADR-0034 cek duplikat masih
-- (pegawai, nomorSk, jenisSk, golonganId), jadi nomor SK yang sama bisa sudah ada di
-- beberapa baris riwayat_sk (jenis/golongan beda); tanpa tanggal_sk join bisa salah pilih.
-- Alur kontrak selalu membuat SK dengan tanggal_sk yang sama dengan baris riwayat_kontrak,
-- sehingga (pegawai_id, nomor_kontrak, tanggal_sk) praktis 1:1 dengan baris SK-nya.
UPDATE `riwayat_kontrak` rk
JOIN `riwayat_sk` sk
    ON sk.pegawai_id = rk.pegawai_id
    AND sk.nomor_sk = rk.nomor_kontrak
    AND sk.tanggal_sk = rk.tanggal_sk
    AND sk.is_deleted = FALSE
SET rk.riwayat_sk_id = sk.id
WHERE rk.riwayat_sk_id IS NULL;

ALTER TABLE `riwayat_kontrak`
    ADD UNIQUE KEY `uk_rwt_ktrk_pgw_id_rwt_sk_id` (`pegawai_id`, `riwayat_sk_id`),
    ADD CONSTRAINT `fk_rwt_ktrk_rwt_sk_rwt_sk_id` FOREIGN KEY (`riwayat_sk_id`) REFERENCES `riwayat_sk` (`id`);

-- Envers: entity RiwayatKontrak kini punya asosiasi riwayatSk -> tabel audit wajib
-- punya kolom yang sama, kalau tidak setiap write kontrak (save/update/delete) gagal
-- dengan "Unknown column 'riwayat_sk_id' in riwayat_kontrak_aud".
ALTER TABLE `riwayat_kontrak_aud`
    ADD COLUMN `riwayat_sk_id` bigint(20) NULL;
