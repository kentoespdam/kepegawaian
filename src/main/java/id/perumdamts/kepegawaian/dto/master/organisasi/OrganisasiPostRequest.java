package id.perumdamts.kepegawaian.dto.master.organisasi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class OrganisasiPostRequest {
    private String kode;
    private Long parentId;
    private Integer levelOrganisasi;
    @NotEmpty(message = "Nama tidak boleh kosong")
    private String nama;
    private String shortName;
    private String category;

    @JsonIgnore
    public Specification<Organisasi> getSpecification() {
        return SpecificationBuilder.<Organisasi>of()
                .addEqual(kode, "kode")
                .addEqual(parentId, "parent", "id")
                .addEqual(levelOrganisasi, "levelOrg")
                .addEqual(nama, "nama")
                .build();
    }
}
