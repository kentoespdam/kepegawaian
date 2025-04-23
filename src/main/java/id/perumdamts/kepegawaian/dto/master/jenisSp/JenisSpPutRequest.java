package id.perumdamts.kepegawaian.dto.master.jenisSp;

import id.perumdamts.kepegawaian.entities.master.JenisSp;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class JenisSpPutRequest extends JenisSpPostRequest {
    public static JenisSp toEntity(JenisSp entity, JenisSpPostRequest request) {
        entity.setKode(request.getKode());
        entity.setNama(request.getNama());
        return entity;
    }
}
