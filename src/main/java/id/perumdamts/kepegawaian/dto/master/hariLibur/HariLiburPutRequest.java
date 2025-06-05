package id.perumdamts.kepegawaian.dto.master.hariLibur;

import id.perumdamts.kepegawaian.entities.master.HariLibur;

public class HariLiburPutRequest extends HariLiburPostRequest {
    public static HariLibur toEntity(HariLibur entity, HariLiburPutRequest request) {
        entity.setTanggal(request.getTanggal());
        entity.setJenisLibur(request.getJenisLibur());
        entity.setNotes(request.getNotes());
        return entity;
    }
}
