package id.perumdamts.kepegawaian.services.master.hariLibur;

import id.perumdamts.kepegawaian.dto.master.hariLibur.HariLiburPostRequest;
import id.perumdamts.kepegawaian.entities.master.HariLibur;

public final class HariLiburMapper {
    private HariLiburMapper() {}

    public static HariLibur toEntity(HariLiburPostRequest request) {
        return new HariLibur(request.getTanggal(), request.getJenisLibur(), request.getNotes());
    }

    public static void updateEntity(HariLibur entity, HariLiburPostRequest request) {
        entity.setTanggal(request.getTanggal());
        entity.setJenisLibur(request.getJenisLibur());
        entity.setNotes(request.getNotes());
    }
}
