package id.perumdamts.kepegawaian.mapper.master.apd;

import id.perumdamts.kepegawaian.dto.master.apd.ApdPostRequest;
import id.perumdamts.kepegawaian.entities.master.Apd;
import id.perumdamts.kepegawaian.entities.master.Profesi;

public final class ApdMapper {
    private ApdMapper() {}

    public static Apd toEntity(ApdPostRequest request, Profesi profesi) {
        return new Apd(profesi, request.getNama());
    }

    public static void updateEntity(Apd entity, ApdPostRequest request, Profesi profesi) {
        entity.setProfesi(profesi);
        entity.setNama(request.getNama());
    }
}
