package id.perumdamts.kepegawaian.dto.profil.keahlian;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EKualifikasi;
import id.perumdamts.kepegawaian.entities.profil.Keahlian;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class KeahlianRequest extends CommonPageRequest {
    private String biodataId;
    private String keahlianId;
    @Enumerated(EnumType.ORDINAL)
    private EKualifikasi kualifikasi;
    private Boolean sertifikasi;
    private String institusi;
    private Integer tahun;

    public Specification<Keahlian> getSpecification() {
        return SpecificationBuilder.<Keahlian>of()
                .addEqual(biodataId, "biodata", "nik")
                .addEqual(keahlianId, "jenisKeahlian", "id")
                .addEqual(kualifikasi, "kualifikasi")
                .addEqual(sertifikasi, "sertifikasi")
                .addLike(institusi, "institusi")
                .addEqual(tahun, "tahun")
                .build();
    }
}
