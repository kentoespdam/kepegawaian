package id.perumdamts.kepegawaian.dto.laporan.kepegawaian;

import java.time.LocalDate;

public record KontrakResponse(
        String nipam,
        String nama,
        String nomorKontrak,
        String namaOrganisasi,
        String namaJabatan,
        LocalDate tanggalMulai,
        LocalDate tanggalSelesai,
        Integer sisaTahun,
        Integer sisaBulan
) {}
