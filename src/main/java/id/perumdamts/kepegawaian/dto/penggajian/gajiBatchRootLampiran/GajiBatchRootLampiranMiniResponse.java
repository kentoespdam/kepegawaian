package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRootLampiran;

import id.perumdamts.kepegawaian.entities.commons.EJenisPotonganGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRootLampiran;

public record GajiBatchRootLampiranMiniResponse(
        Long id,
        EJenisPotonganGaji jenisLampiranGaji,
        String fileName,
        String mimeType
) {
    public static GajiBatchRootLampiranMiniResponse from(GajiBatchRootLampiran entity) {
        return new GajiBatchRootLampiranMiniResponse(
                entity.getId(),
                entity.getJenisLampiranGaji(),
                entity.getFileName(),
                entity.getMimeType()
        );
    }
}
