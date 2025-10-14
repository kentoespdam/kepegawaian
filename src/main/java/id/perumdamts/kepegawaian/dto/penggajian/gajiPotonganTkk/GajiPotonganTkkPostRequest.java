package id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPotonganTkk;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

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
        return SpecificationBuilder.<GajiPotonganTkk>of()
                .addEqual(statusPegawai, "statusPegawai")
                .addEqual(levelId, "level", "id")
                .addEqual(golonganId, "golongan", "id")
                .build();
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
