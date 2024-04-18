package id.perumdamts.kepegawaian.dto.master.jenisPelatihan;

import id.perumdamts.kepegawaian.entities.master.JenisPelatihan;

public class JenisPelatihanPutRequest extends JenisPelatihanPostRequest{
    public static JenisPelatihan toEntity(JenisPelatihanPutRequest request, JenisPelatihan entity) {
        entity.setNama(request.getNama());
        return entity;
    }
}
