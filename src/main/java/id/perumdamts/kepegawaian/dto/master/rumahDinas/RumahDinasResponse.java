package id.perumdamts.kepegawaian.dto.master.rumahDinas;

import id.perumdamts.kepegawaian.entities.master.RumahDinas;

public record RumahDinasResponse(
        Long id,
        String nama,
        Double nilai
) {
    public static RumahDinasResponse from(RumahDinas entity) {
        return new RumahDinasResponse(entity.getId(), entity.getNama(), entity.getNilai());
    }
}
