package id.perumdamts.kepegawaian.dto.profil.keahlian;

import id.perumdamts.kepegawaian.dto.master.jenisKeahlian.JenisKeahlianResponse;

import java.time.LocalDateTime;

public record KeahlianQuery(
        Long id,
        String biodataId,
        String biodataNik,
        String biodataNama,
        JenisKeahlianResponse jenisKeahlian,
        String kualifikasi,
        Boolean sertifikasi,
        String institusi,
        Integer tahun,
        String masaBerlaku,
        Boolean disetujui,
        LocalDateTime tanggalPengajuan,
        LocalDateTime tanggalDisetujui,
        String disetujuiOleh,
        Byte changedStatus
) {}
