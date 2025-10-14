package id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.commons.EJenisTunjangan;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.penggajian.GajiTunjangan;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class GajiTunjanganPostRequest {
    @Enumerated(EnumType.ORDINAL)
    private EJenisTunjangan jenisTunjangan;
    private Long levelId;
    private Long golonganId;
    private Double nominal;

    @JsonIgnore
    public Specification<GajiTunjangan> getSpecification() {
        return SpecificationBuilder.<GajiTunjangan>of()
                .addEqual(jenisTunjangan, "jenisTunjangan")
                .addEqual(levelId, "level", "id")
                .addEqual(golonganId, "golongan", "id")
                .build();
    }

    public static GajiTunjangan toEntity(GajiTunjanganPostRequest request, Level level, Golongan golongan) {
        GajiTunjangan entity = new GajiTunjangan();
        entity.setJenisTunjangan(request.getJenisTunjangan());
        entity.setLevel(level);
        entity.setGolongan(golongan);
        entity.setNominal(request.getNominal());
        return entity;
    }
}
