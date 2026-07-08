package id.perumdamts.kepegawaian.dto.profil.keluarga;

import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanResponse;

import java.time.LocalDate;

public record ProfilKeluargaQuery(
        Long id,
        String biodataId,
        String biodataNik,
        String biodataNama,
        String nik,
        String nama,
        String jenisKelamin,
        String agama,
        String hubunganKeluarga,
        String tempatLahir,
        LocalDate tanggalLahir,
        Boolean tanggungan,
        Long pendidikanId,
        JenjangPendidikanResponse jenjangPendidikan,
        String statusPendidikan,
        Boolean statusKawin,
        String notes,
        Integer version,
        Boolean isDeleted,
        Boolean changedStatus
) {}