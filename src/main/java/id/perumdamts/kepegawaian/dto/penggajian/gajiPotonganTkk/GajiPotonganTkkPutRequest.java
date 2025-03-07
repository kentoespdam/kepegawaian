package id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk;

import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPotonganTkk;

public class GajiPotonganTkkPutRequest extends GajiPotonganTkkPostRequest {
    public static GajiPotonganTkk toEntity(GajiPotonganTkk entity, GajiPotonganTkkPutRequest request, Level level, Golongan golongan) {
        entity.setStatusPegawai(request.getStatusPegawai());
        entity.setLevel(level);
        entity.setGolongan(golongan);
        entity.setNominal(request.getNominal());
        return entity;
    }
}
