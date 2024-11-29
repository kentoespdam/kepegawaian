package id.perumdamts.kepegawaian.dto.master.rumahDinas;


import id.perumdamts.kepegawaian.entities.master.RumahDinas;

public class RumahDinasPutRequest extends RumahDinasPostRequest {
    public static RumahDinas toEntity(RumahDinas entity, RumahDinasPutRequest request) {
        entity.setNama(request.getNama());
        entity.setNilai(request.getNilai());
        return entity;
    }
}
