package id.perumdamts.kepegawaian.dto.master.profesi;

import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProfesiPutRequest extends ProfesiPostRequest {
    public static Profesi toEntity(Profesi entity, ProfesiPostRequest request, Level level) {
        entity.setLevel(level);
        entity.setNama(request.getNama());
        entity.setDetail(request.getDetail());
        entity.setResiko(request.getResiko());
        return entity;
    }
}
