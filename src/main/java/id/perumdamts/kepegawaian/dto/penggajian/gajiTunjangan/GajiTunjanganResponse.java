package id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan;

import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisTunjangan;
import id.perumdamts.kepegawaian.entities.penggajian.GajiTunjangan;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GajiTunjanganResponse {
    private Long id;
    @Enumerated(EnumType.ORDINAL)
    private EJenisTunjangan jenisTunjangan;
    private LevelResponse level;
    private GolonganResponse golongan;
    private Double nominal;

    public static GajiTunjanganResponse from(GajiTunjangan entity) {
        GajiTunjanganResponse result = new GajiTunjanganResponse();
        result.setId(entity.getId());
        result.setJenisTunjangan(entity.getJenisTunjangan());
        result.setLevel(LevelResponse.from(entity.getLevel()));
        if (entity.getGolongan() != null)
            result.setGolongan(GolonganResponse.from(entity.getGolongan()));
        result.setNominal(entity.getNominal());
        return result;
    }

}
