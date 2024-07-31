package id.perumdamts.kepegawaian.dto.master.alasanBerhenti;

import id.perumdamts.kepegawaian.entities.master.AlasanBerhenti;

public class AlasanBerhentiPutRequest extends AlasanBerhentiPostRequest {
    public static AlasanBerhenti toEntity(AlasanBerhenti entity,AlasanBerhentiPostRequest request) {
        entity.setNama(request.getNama());
        entity.setNotes(request.getNotes());
        return entity;
    }
}
