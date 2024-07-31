package id.perumdamts.kepegawaian.dto.master.jenisSp;

import id.perumdamts.kepegawaian.entities.master.JenisSp;

public class JenisSpPutRequest extends JenisSpPostRequest {
    public static JenisSp toEntity(JenisSp entity, JenisSpPutRequest request) {
        entity.setNama(request.getNama());
        entity.setNotes(request.getNotes());
        return entity;
    }
}
