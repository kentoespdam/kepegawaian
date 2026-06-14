package id.perumdamts.kepegawaian.services.master.rumahDinas;

import id.perumdamts.kepegawaian.dto.master.rumahDinas.RumahDinasPostRequest;
import id.perumdamts.kepegawaian.entities.master.RumahDinas;

public final class RumahDinasMapper {
    private RumahDinasMapper() {}

    public static RumahDinas toEntity(RumahDinasPostRequest request) {
        return new RumahDinas(null, request.getNama(), request.getNilai());
    }

    public static void updateEntity(RumahDinas entity, RumahDinasPostRequest request) {
        entity.setNama(request.getNama());
        entity.setNilai(request.getNilai());
    }
}
