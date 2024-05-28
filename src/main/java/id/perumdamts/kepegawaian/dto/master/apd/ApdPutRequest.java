package id.perumdamts.kepegawaian.dto.master.apd;

import id.perumdamts.kepegawaian.entities.master.Apd;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApdPutRequest extends ApdPostRequest {
    public static Apd toEntity(Apd entity, ApdPutRequest request, Profesi profesi) {
        entity.setProfesi(profesi);
        entity.setNama(request.getNama());
        return entity;
    }
}
