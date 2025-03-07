package id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPotonganTkk;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@Data
public class GajiPotonganTkkPostRequest {
    @NotNull(message = "Pegawai ID is required")
    @Enumerated(EnumType.ORDINAL)
    private EStatusPegawai statusPegawai;
    private Long levelId;
    private Long golonganId;
    private Double nominal;

    @JsonIgnore
    public Specification<GajiPotonganTkk> getSpecification() {
        Specification<GajiPotonganTkk> statusSpec = Objects.isNull(statusPegawai) ? null :
                (root, query, cb) -> cb.equal(root.get("statusPegawai"), statusPegawai);
        Specification<GajiPotonganTkk> levelSpec = Objects.isNull(levelId) || levelId <= 0 ? null :
                (root, query, cb) -> cb.equal(root.get("level").get("id"), levelId);
        Specification<GajiPotonganTkk> golonganSpec = Objects.isNull(golonganId) || golonganId <= 0 ? null :
                (root, query, cb) -> cb.equal(root.get("golongan").get("id"), golonganId);
        return Specification.where(statusSpec).and(levelSpec).and(golonganSpec);
    }

    public static GajiPotonganTkk toEntity(GajiPotonganTkkPostRequest request, Level level, Golongan golongan) {
        GajiPotonganTkk entity = new GajiPotonganTkk();
        entity.setStatusPegawai(request.getStatusPegawai());
        entity.setLevel(level);
        entity.setGolongan(golongan);
        entity.setNominal(request.getNominal());
        return entity;
    }
}
