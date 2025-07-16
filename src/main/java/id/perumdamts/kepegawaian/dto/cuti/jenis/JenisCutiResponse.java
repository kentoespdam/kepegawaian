package id.perumdamts.kepegawaian.dto.cuti.jenis;

import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class JenisCutiResponse extends JenisCutiMiniResponse {
    private JenisCutiMiniResponse parent;

    public static JenisCutiResponse from(CutiJenis entity) {
        if (entity == null) return null;
        JenisCutiResponse response = new JenisCutiResponse();
        response.setId(entity.getId());
        response.setNama(entity.getNama());
        if (entity.getParent() != null)
            response.setParent(JenisCutiMiniResponse.from(entity.getParent()));
        return response;
    }
}
