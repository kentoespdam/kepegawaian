package id.perumdamts.kepegawaian.dto.profil.lampiranProfil;

import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.profil.LampiranProfil;

import java.time.LocalDateTime;

public record LampiranProfilResponse(
        Long id,
        EJenisLampiranProfil ref,
        Long refId,
        String mimeType,
        String fileName,
        String notes,
        Boolean disetujui,
        LocalDateTime tanggalPengajuan,
        LocalDateTime tanggalDisetujui,
        String disetujuiOleh
) {
    public static LampiranProfilResponse from(LampiranProfil entity) {
        return new LampiranProfilResponse(
                entity.getId(),
                entity.getRef(),
                entity.getRefId(),
                entity.getMimeType(),
                entity.getFileName(),
                entity.getNotes(),
                entity.getDisetujui(),
                entity.getTanggalPengajuan(),
                entity.getTanggalDisetujui(),
                entity.getDisetujuiOleh()
        );
    }
}
