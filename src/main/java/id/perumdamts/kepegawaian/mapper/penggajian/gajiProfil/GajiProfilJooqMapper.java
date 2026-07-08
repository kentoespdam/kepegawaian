package id.perumdamts.kepegawaian.mapper.penggajian.gajiProfil;

import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilResponse;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.GajiProfil.GAJI_PROFIL;

public final class GajiProfilJooqMapper {
    private GajiProfilJooqMapper() {}

    public static GajiProfilResponse mapToResponse(Record record) {
        if (record == null) return null;
        return new GajiProfilResponse(
                record.get(GAJI_PROFIL.ID),
                record.get(GAJI_PROFIL.NAMA)
        );
    }
}
