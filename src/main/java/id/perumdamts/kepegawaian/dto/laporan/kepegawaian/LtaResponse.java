package id.perumdamts.kepegawaian.dto.laporan.kepegawaian;

import java.time.LocalDate;

public record LtaResponse(
        Long id,
        String namaAnak,
        String jenisKelamin,
        LocalDate tanggalLahir,
        Integer umur,
        Boolean tanggungan,
        String statusPendidikan,
        String namaKaryawan,
        String nipam,
        String namaJabatan
) {}
