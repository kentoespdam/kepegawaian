package id.perumdamts.kepegawaian.dto.master.jenisKeahlian;

import id.perumdamts.kepegawaian.entities.master.JenisKeahlian;

public class JenisKeahlianPutRequest extends JenisKeahlianPostRequest {

    public static JenisKeahlian toEntity(JenisKeahlianPutRequest request, JenisKeahlian entity) {
        entity.setNama(request.getNama());
        return entity;
    }
}
