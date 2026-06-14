package id.perumdamts.kepegawaian.mapper.master.alasanBerhenti;

import id.perumdamts.kepegawaian.dto.master.alasanBerhenti.AlasanBerhentiPostRequest;
import id.perumdamts.kepegawaian.entities.master.AlasanBerhenti;

public final class AlasanBerhentiMapper {
    private AlasanBerhentiMapper() {}

    public static AlasanBerhenti toEntity(AlasanBerhentiPostRequest request) {
        return new AlasanBerhenti(request.getNama(), request.getNotes());
    }

    public static void updateEntity(AlasanBerhenti entity, AlasanBerhentiPostRequest request) {
        entity.setNama(request.getNama());
        entity.setNotes(request.getNotes());
    }
}