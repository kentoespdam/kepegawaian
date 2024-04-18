package id.perumdamts.kepegawaian.dto.master.jenjangPendidikan;

import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;

public class JenjangPendidikanPutRequest extends JenjangPendidikanPostRequest {
    public static JenjangPendidikan toEntity(JenjangPendidikanPutRequest request, JenjangPendidikan entity) {
        entity.setNama(request.getNama());
        entity.setSeq(request.getSeq());
        return entity;
    }
}
