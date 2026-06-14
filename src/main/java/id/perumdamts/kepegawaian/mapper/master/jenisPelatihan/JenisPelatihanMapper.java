package id.perumdamts.kepegawaian.mapper.master.jenisPelatihan;

import id.perumdamts.kepegawaian.dto.master.jenisPelatihan.JenisPelatihanPostRequest;
import id.perumdamts.kepegawaian.entities.master.JenisPelatihan;

public final class JenisPelatihanMapper {
    private JenisPelatihanMapper() {}

    public static JenisPelatihan toEntity(JenisPelatihanPostRequest request) {
        return new JenisPelatihan(request.getNama());
    }

    public static void updateEntity(JenisPelatihan entity, JenisPelatihanPostRequest request) {
        entity.setNama(request.getNama());
    }
}
