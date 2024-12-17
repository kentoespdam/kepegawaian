package id.perumdamts.kepegawaian.dto.master.kodePajak;

import id.perumdamts.kepegawaian.entities.master.KodePajak;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class KodePajakPutRequest extends KodePajakPostRequest {
    public static KodePajak toEntity(KodePajak entity, KodePajakPutRequest request) {
        entity.setKode(request.getKode());
        entity.setNominal(request.getNominal());
        return entity;
    }
}
