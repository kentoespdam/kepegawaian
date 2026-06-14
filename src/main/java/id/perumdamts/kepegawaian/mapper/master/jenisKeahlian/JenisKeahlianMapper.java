package id.perumdamts.kepegawaian.mapper.master.jenisKeahlian;

import id.perumdamts.kepegawaian.dto.master.jenisKeahlian.JenisKeahlianPostRequest;
import id.perumdamts.kepegawaian.entities.master.JenisKeahlian;

public final class JenisKeahlianMapper {
    private JenisKeahlianMapper() {}

    public static JenisKeahlian toEntity(JenisKeahlianPostRequest request) {
        return new JenisKeahlian(request.getNama());
    }

    public static void updateEntity(JenisKeahlian entity, JenisKeahlianPostRequest request) {
        entity.setNama(request.getNama());
    }
}
