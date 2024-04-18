package id.perumdamts.kepegawaian.dto.master.jenisKitas;

import id.perumdamts.kepegawaian.entities.master.JenisKitas;

public class JenisKitasPutRequest extends JenisKitasPostRequest {
    public static JenisKitas toEntity(JenisKitasPutRequest request, JenisKitas entity) {
        entity.setNama(request.getNama());
        return entity;
    }
}
