package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRootLampiran;

import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRootLampiran;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GajiBatchRootLampiranMiniResponse {
    public Long id;
    public String fileName;
    public String mimeType;

    public static GajiBatchRootLampiranMiniResponse from(GajiBatchRootLampiran entity) {
        return new GajiBatchRootLampiranMiniResponse(entity.getId(), entity.getFileName(), entity.getMimeType());
    }

    public static List<GajiBatchRootLampiranMiniResponse> from(List<GajiBatchRootLampiran> entities) {
        if (entities == null) return null;
        return entities.stream().map(GajiBatchRootLampiranMiniResponse::from).toList();
    }
}
