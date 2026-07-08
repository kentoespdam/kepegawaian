package id.perumdamts.kepegawaian.dto.master.alasanBerhenti;

import id.perumdamts.kepegawaian.entities.master.AlasanBerhenti;

public record AlasanBerhentiResponse(
        Long id,
        String nama,
        String notes
) {
    public static AlasanBerhentiResponse from(AlasanBerhenti entity) {
        return new AlasanBerhentiResponse(entity.getId(), entity.getNama(), entity.getNotes());
    }
}
