package id.perumdamts.kepegawaian.dto.profil.pengalamanKerja;

import id.perumdamts.kepegawaian.entities.profil.PengalamanKerja;

import java.time.LocalDateTime;

public record PengalamanKerjaResponse(
        Long id,
        String biodataId,
        String biodataNama,
        String namaPerusahaan,
        String typePerusahaan,
        String jabatan,
        String lokasi,
        Integer tahunMasuk,
        Integer tahunKeluar,
        String notes,
        Boolean disetujui,
        LocalDateTime tanggalPengajuan,
        LocalDateTime tanggalDisetujui,
        String disetujuiOleh,
        Boolean changedStatus
) {
    public static PengalamanKerjaResponse from(PengalamanKerja entity) {
        return new PengalamanKerjaResponse(
                entity.getId(),
                entity.getBiodata().getNik(),
                entity.getBiodata().getNama(),
                entity.getNamaPerusahaan(),
                entity.getTypePerusahaan(),
                entity.getJabatan(),
                entity.getLokasi(),
                entity.getTahunMasuk(),
                entity.getTahunKeluar(),
                entity.getNotes(),
                entity.getDisetujui(),
                entity.getTanggalPengajuan(),
                entity.getTanggalDisetujui(),
                entity.getDisetujuiOleh(),
                entity.getChangedStatus()
        );
    }
}
