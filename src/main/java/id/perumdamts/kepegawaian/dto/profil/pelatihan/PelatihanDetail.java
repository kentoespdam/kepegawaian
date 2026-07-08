package id.perumdamts.kepegawaian.dto.profil.pelatihan;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;

import java.time.LocalDate;
import java.util.List;

public record PelatihanDetail(
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
        Byte changedStatus,
        List<LampiranRow> lampiran
) {}