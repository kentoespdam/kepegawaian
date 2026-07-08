package id.perumdamts.kepegawaian.dto.profil.pengalamanKerja;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;

import java.util.List;

public record PengalamanKerjaDetail(
        Long id,
        String biodataId,
        String biodataNik,
        String biodataNama,
        String namaPerusahaan,
        String typePerusahaan,
        String jabatan,
        String lokasi,
        Integer tahunMasuk,
        Integer tahunKeluar,
        String notes,
        Byte changedStatus,
        List<LampiranRow> lampiran
) {}