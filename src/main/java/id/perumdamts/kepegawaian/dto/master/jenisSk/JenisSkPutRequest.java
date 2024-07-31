package id.perumdamts.kepegawaian.dto.master.jenisSk;

import id.perumdamts.kepegawaian.entities.master.JenisSk;

public class JenisSkPutRequest extends JenisSkPostRequest {
    public static JenisSk toEntity(JenisSk entity, JenisSkPutRequest request) {
        entity.setNama(request.getNama());
        entity.setNotes(request.getNotes());
        return entity;
    }
}
