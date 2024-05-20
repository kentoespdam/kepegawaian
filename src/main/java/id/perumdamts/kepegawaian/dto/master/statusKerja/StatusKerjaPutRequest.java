package id.perumdamts.kepegawaian.dto.master.statusKerja;

import id.perumdamts.kepegawaian.entities.master.StatusKerja;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class StatusKerjaPutRequest extends StatusKerjaPostRequest {
    public static StatusKerja toEntity(StatusKerja entity, StatusKerjaPutRequest request) {
        entity.setNama(request.getNama());
        return entity;
    }
}
