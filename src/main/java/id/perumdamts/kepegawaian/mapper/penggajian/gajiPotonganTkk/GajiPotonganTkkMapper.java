package id.perumdamts.kepegawaian.mapper.penggajian.gajiPotonganTkk;

import id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk.GajiPotonganTkkPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk.GajiPotonganTkkPutRequest;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPotonganTkk;

public final class GajiPotonganTkkMapper {
    private GajiPotonganTkkMapper() {}

    public static GajiPotonganTkk toEntity(GajiPotonganTkkPostRequest request, Level level, Golongan golongan) {
        GajiPotonganTkk entity = new GajiPotonganTkk();
        entity.setStatusPegawai(request.getStatusPegawai());
        entity.setLevel(level);
        entity.setGolongan(golongan);
        entity.setNominal(request.getNominal());
        return entity;
    }

    public static void updateEntity(GajiPotonganTkk entity, GajiPotonganTkkPutRequest request, Level level, Golongan golongan) {
        entity.setStatusPegawai(request.getStatusPegawai());
        entity.setLevel(level);
        entity.setGolongan(golongan);
        entity.setNominal(request.getNominal());
    }
}
