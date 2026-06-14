package id.perumdamts.kepegawaian.dto.master.organisasi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrganisasiIndexQuery extends CommonPageRequest {
    private String kode;
    private String nama;
    private Long parentId;
    private Integer levelOrg;
    private String category;

    @JsonIgnore
    public Specification<Organisasi> getSpecification() {
        return SpecificationBuilder.<Organisasi>of()
                .addLike(kode, "kode")
                .addLike(nama, "nama")
                .addEqual(parentId, "parent", "id")
                .addEqual(levelOrg, "levelOrg")
                .addEqual(category, "category")
                .build();
    }
}
