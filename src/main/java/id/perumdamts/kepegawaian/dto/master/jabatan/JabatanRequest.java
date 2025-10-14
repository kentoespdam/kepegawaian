package id.perumdamts.kepegawaian.dto.master.jabatan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class JabatanRequest extends CommonPageRequest {
    private String kode;
    private Long parentId;
    private Long organisasiId;
    private Long levelId;
    private String nama;

    @JsonIgnore
    public Specification<Jabatan> getSpecification() {
        return SpecificationBuilder.<Jabatan>of()
                .addEqual(kode, "kode")
                .addEqual(parentId, "parent", "id")
                .addEqual(organisasiId, "organisasi", "id")
                .addEqual(levelId, "level", "id")
                .addEqual(nama, "nama")
                .build();
    }
}
