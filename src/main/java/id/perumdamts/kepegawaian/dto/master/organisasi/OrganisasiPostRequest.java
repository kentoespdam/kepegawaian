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

    /**
     * Kunci keunikan Organisasi: nama + parent.
     * Ditetapkan 2026-06-18 (kepegawaian-jow) — lihat CONTEXT.md.
     * Dua record dianggap "sama" jika nama DAN parent-nya sama; kode dan level
     * TIDAK masuk kunci. Dipakai tunggal oleh create() & update() di
     * OrganisasiCommandService (seam eksplisit, bukan konvensi getSpecification()).
     */
    @JsonIgnore
    public Specification<Organisasi> uniquenessSpecification() {
        return SpecificationBuilder.<Organisasi>of()
                .addEqual(parentId, "parent", "id")
                .addEqual(nama, "nama")
                .build();
    }
}
