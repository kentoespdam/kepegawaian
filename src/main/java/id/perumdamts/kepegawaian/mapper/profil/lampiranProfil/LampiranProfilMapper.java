package id.perumdamts.kepegawaian.mapper.profil.lampiranProfil;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilPostRequest;
import id.perumdamts.kepegawaian.entities.profil.LampiranProfil;

import java.time.LocalDateTime;

public final class LampiranProfilMapper {
    private LampiranProfilMapper() {}

    public static LampiranProfil toEntity(LampiranProfilPostRequest request, String fileName, String hashedFileName, String mimeType) {
        LampiranProfil entity = new LampiranProfil();
        entity.setRef(request.getRef());
        entity.setRefId(request.getRefId());
        entity.setFileName(fileName);
        entity.setNotes(request.getNotes());
        entity.setHashedFileName(hashedFileName);
        entity.setMimeType(mimeType);
        entity.setDisetujui(true);
        entity.setTanggalPengajuan(LocalDateTime.now());
        return entity;
    }

}
