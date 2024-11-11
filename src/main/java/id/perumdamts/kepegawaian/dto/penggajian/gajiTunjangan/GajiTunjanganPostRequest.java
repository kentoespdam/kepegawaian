package id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.commons.EJenisTunjangan;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.penggajian.GajiTunjangan;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@Data
public class GajiTunjanganPostRequest {
    @Enumerated(EnumType.ORDINAL)
    private EJenisTunjangan jenisTunjangan;
    private Long levelId;
    private Long golonganId;
    private Double nominal;

    @JsonIgnore
    public Specification<GajiTunjangan> getSpecification() {
        Specification<GajiTunjangan> jenisSpec = Objects.isNull(jenisTunjangan) ? null :
                (root, query, cb) -> cb.equal(root.get("jenisTunjangan"), jenisTunjangan);
        Specification<GajiTunjangan> levelSpec = Objects.isNull(levelId) ? null :
                (root, query, cb) -> cb.equal(root.get("level").get("id"), levelId);
        Specification<GajiTunjangan> golonganSpec = Objects.isNull(golonganId) ? null :
                (root, query, cb) -> cb.equal(root.get("golongan").get("id"), golonganId);
        return Specification.where(jenisSpec).and(levelSpec).and(golonganSpec);
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
