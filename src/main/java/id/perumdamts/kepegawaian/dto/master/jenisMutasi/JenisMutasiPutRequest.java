package id.perumdamts.kepegawaian.dto.master.jenisMutasi;

import id.perumdamts.kepegawaian.entities.master.JenisMutasi;

public class JenisMutasiPutRequest extends JenisMutasiPostRequest {

    public static JenisMutasi toEntity(JenisMutasi entity, JenisMutasiPutRequest request) {
        entity.setNama(request.getNama());
        entity.setNotes(request.getNotes());
        return entity;
    }
}
