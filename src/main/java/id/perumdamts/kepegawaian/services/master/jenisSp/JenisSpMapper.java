package id.perumdamts.kepegawaian.services.master.jenisSp;

import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpPostRequest;
import id.perumdamts.kepegawaian.entities.master.JenisSp;

public final class JenisSpMapper {
    private JenisSpMapper() {}

    public static JenisSp toEntity(JenisSpPostRequest request) {
        JenisSp entity = new JenisSp();
        entity.setKode(request.getKode());
        entity.setNama(request.getNama());
        return entity;
    }

    public static void updateEntity(JenisSp entity, JenisSpPostRequest request) {
        entity.setKode(request.getKode());
        entity.setNama(request.getNama());
    }
}
