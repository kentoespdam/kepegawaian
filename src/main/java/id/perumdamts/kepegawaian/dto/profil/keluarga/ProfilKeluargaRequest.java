package id.perumdamts.kepegawaian.dto.profil.keluarga;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EHubunganKeluarga;
import id.perumdamts.kepegawaian.entities.profil.ProfilKeluarga;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProfilKeluargaRequest extends CommonPageRequest {
    private String biodataId;
    private String nik;
    private String nama;
    @Enumerated(EnumType.ORDINAL)
    private EHubunganKeluarga hubunganKeluarga;
    private Boolean tanggungan;
    private Boolean statusKawin;

    @JsonIgnore
    public Specification<ProfilKeluarga> getSpecification() {
        return SpecificationBuilder.<ProfilKeluarga>of()
                .addEqual(biodataId, "biodata", "nik")
                .addEqual(nik, "nik")
                .addLike(nama, "nama")
                .addEqual(hubunganKeluarga, "hubunganKeluarga")
                .addEqual(tanggungan, "tanggungan")
                .addEqual(statusKawin, "statusKawin")
                .build();
    }
}
