package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRootLampiran;

import id.perumdamts.kepegawaian.entities.commons.EJenisPotonganGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRootLampiran;
import lombok.Data;

@Data
public class GajiBatchRootLampiranMiniResponse {
    private Long id;
    private EJenisPotonganGaji jenisLampiranGaji;
    private String fileName;
    private String mimeType;

    public static GajiBatchRootLampiranMiniResponse from(GajiBatchRootLampiran entity) {
        GajiBatchRootLampiranMiniResponse response = new GajiBatchRootLampiranMiniResponse();
        response.setId(entity.getId());
        response.setJenisLampiranGaji(entity.getJenisLampiranGaji());
        response.setFileName(entity.getFileName());
        response.setMimeType(entity.getMimeType());
        return response;
    }
}
