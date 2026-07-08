package id.perumdamts.kepegawaian.dto.profil.pengalamanKerja;

public record PengalamanKerjaQuery(
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
        Byte changedStatus
) {}