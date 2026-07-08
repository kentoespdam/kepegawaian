package id.perumdamts.kepegawaian.dto.profil.pelatihan;

import java.time.LocalDate;

public record PelatihanQuery(
        Long id,
        String biodataId,
        String biodataNik,
        String biodataNama,
        Long jenisPelatihanId,
        String jenisPelatihanNama,
        String nama,
        String lembaga,
        LocalDate tanggalMulai,
        LocalDate tanggalSelesai,
        Boolean lulus,
        String nilai,
        Boolean ikatanDinas,
        LocalDate tanggalAkhirIkatan,
        String notes,
        Byte changedStatus
) {}