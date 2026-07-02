package id.perumdamts.kepegawaian.mapper.penggajian.gajiTunjangan;

import id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan.GajiTunjanganPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan.GajiTunjanganPutRequest;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.penggajian.GajiTunjangan;

public final class GajiTunjanganMapper {
    private GajiTunjanganMapper() {}

    public static GajiTunjangan toEntity(GajiTunjanganPostRequest request, Level level, Golongan golongan) {
        GajiTunjangan entity = new GajiTunjangan();
        entity.setJenisTunjangan(request.getJenisTunjangan());
        entity.setLevel(level);
        entity.setGolongan(golongan);
        entity.setNominal(request.getNominal());
        return entity;
    }

    public static void updateEntity(GajiTunjangan entity, GajiTunjanganPutRequest request, Level level, Golongan golongan) {
        entity.setJenisTunjangan(request.getJenisTunjangan());
        entity.setLevel(level);
        entity.setGolongan(golongan);
        entity.setNominal(request.getNominal());
    }
}
