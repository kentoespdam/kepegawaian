package id.perumdamts.kepegawaian.services.master.alatKerja;

import id.perumdamts.kepegawaian.dto.master.alatKerja.AlatKerjaPostRequest;
import id.perumdamts.kepegawaian.entities.master.AlatKerja;
import id.perumdamts.kepegawaian.entities.master.Profesi;

public final class AlatKerjaMapper {
    private AlatKerjaMapper() {}

    public static AlatKerja toEntity(AlatKerjaPostRequest request, Profesi profesi) {
        return new AlatKerja(profesi, request.getNama());
    }

    public static void updateEntity(AlatKerja entity, AlatKerjaPostRequest request, Profesi profesi) {
        entity.setProfesi(profesi);
        entity.setNama(request.getNama());
    }
}
