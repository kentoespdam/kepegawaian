package id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan;

import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.penggajian.GajiTunjangan;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiTunjanganPutRequest extends GajiTunjanganPostRequest {
    public static GajiTunjangan toEntity(GajiTunjangan entity, GajiTunjanganPutRequest request, Level level, Golongan golongan) {
        entity.setJenisTunjangan(request.getJenisTunjangan());
        entity.setLevel(level);
        entity.setGolongan(golongan);
        entity.setNominal(request.getNominal());
        return entity;
    }
}
