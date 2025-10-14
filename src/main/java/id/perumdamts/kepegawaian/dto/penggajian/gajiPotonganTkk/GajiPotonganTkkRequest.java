package id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPotonganTkk;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiPotonganTkkRequest extends CommonPageRequest {
    private EStatusPegawai statusPegawai;
    private Long levelId;
    private Long golonganId;

    public Specification<GajiPotonganTkk> getSpecification() {
        return SpecificationBuilder.<GajiPotonganTkk>of()
                .addEqual(statusPegawai, "statusPegawai")
                .addEqual(levelId, "level", "id")
                .addEqual(golonganId, "golongan", "id")
                .build();
    }
}
