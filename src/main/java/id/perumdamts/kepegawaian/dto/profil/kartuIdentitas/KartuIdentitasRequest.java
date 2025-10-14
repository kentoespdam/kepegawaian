package id.perumdamts.kepegawaian.dto.profil.kartuIdentitas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.profil.KartuIdentitas;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class KartuIdentitasRequest extends CommonPageRequest {
    private String nik;
    private Long jenisKartuId;
    private String nomorKartu;

    @JsonIgnore
    public Specification<KartuIdentitas> getSpecification() {
        return SpecificationBuilder.<KartuIdentitas>of()
                .addEqual(nik, "biodata", "nik")
                .addEqual(jenisKartuId, "jenisKartu", "id")
                .addEqual(nomorKartu, "nomorKartu")
                .build();
    }
}
