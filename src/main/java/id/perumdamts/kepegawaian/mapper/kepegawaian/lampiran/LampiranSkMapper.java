package id.perumdamts.kepegawaian.mapper.kepegawaian.lampiran;

import id.perumdamts.kepegawaian.dto.kepegawaian.lampiran.LampiranSkPostRequest;
import id.perumdamts.kepegawaian.entities.kepegawaian.LampiranSk;

import java.time.LocalDateTime;

public final class LampiranSkMapper {
    private LampiranSkMapper() {}

    public static LampiranSk toEntity(LampiranSkPostRequest request, String fileName, String hashedFileName, String mimeType) {
        LampiranSk entity = new LampiranSk();
        entity.setRef(request.getRef());
        entity.setRefId(request.getRefId());
        entity.setFileName(fileName);
        entity.setNotes(request.getNotes());
        entity.setHashedFileName(hashedFileName);
        entity.setMimeType(mimeType);
        entity.setDisetujui(false);
        entity.setTanggalPengajuan(LocalDateTime.now());
        return entity;
    }

    public static LampiranSk acceptEntity(LampiranSk entity, String oleh) {
        entity.setDisetujui(true);
        entity.setDisetujuiOleh(oleh);
        entity.setTanggalDisetujui(LocalDateTime.now());
        return entity;
    }
}
