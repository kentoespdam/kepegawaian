package id.perumdamts.kepegawaian.dto.master.alatKerja;

import id.perumdamts.kepegawaian.entities.master.AlatKerja;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AlatKerjaPutRequest extends AlatKerjaPostRequest {
    public static AlatKerja toEntity(AlatKerja entity, AlatKerjaPutRequest request, Profesi profesi) {
        entity.setNama(request.getNama());
        entity.setProfesi(profesi);
        return entity;
    }
}
