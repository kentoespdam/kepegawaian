package id.perumdamts.kepegawaian.dto.profil.pelatihan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.profil.Pelatihan;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class PelatihanRequest extends CommonPageRequest {
    private String biodataId;
    private String jenisPelatihanId;
    private String nama;
    private Boolean lulus;
    private Boolean disetujui;

    @JsonIgnore
    public Specification<Pelatihan> getSpecification() {
        return SpecificationBuilder.<Pelatihan>of()
                .addEqual(biodataId, "biodata", "nik")
                .addEqual(jenisPelatihanId, "jenisPelatihan", "id")
                .addLike(nama, "nama")
                .addEqual(lulus, "lulus")
                .addEqual(disetujui, "disetujui")
                .build();
    }
}
