package id.perumdamts.kepegawaian.services.master.golongan;

import id.perumdamts.kepegawaian.dto.master.golongan.GolonganPostRequest;
import id.perumdamts.kepegawaian.entities.master.Golongan;

public final class GolonganMapper {
    private GolonganMapper() {}

    public static Golongan toEntity(GolonganPostRequest request) {
        return new Golongan(request.getGolongan(), request.getPangkat());
    }

    public static void updateEntity(Golongan entity, GolonganPostRequest request) {
        entity.setGolongan(request.getGolongan());
        entity.setPangkat(request.getPangkat());
    }
}
