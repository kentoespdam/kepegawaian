package id.perumdamts.kepegawaian.dto.profil.pendidikan;

import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanResponse;

import java.time.LocalDateTime;

public record PendidikanQuery(
        Long id,
        String biodataId,
        String biodataNik,
        String biodataNama,
        Long jenjangId,
        JenjangPendidikanResponse jenjangPendidikan,
        String gelarDepan,
        String gelarBelakang,
        String jurusan,
        String institusi,
        String kota,
        Integer tahunMasuk,
        Boolean isLulus,
        Integer tahunLulus,
        Double gpa,
        Boolean isLatest,
        Boolean disetujui,
        LocalDateTime tanggalPengajuan,
        LocalDateTime tanggalDisetujui,
        String disetujuiOleh,
        Byte changedStatus
) {}
