package id.perumdamts.kepegawaian.dto.profil.keahlian;

import org.springframework.data.jpa.domain.Specification;

import com.fasterxml.jackson.annotation.JsonIgnore;

import id.perumdamts.kepegawaian.entities.commons.EKualifikasi;
import id.perumdamts.kepegawaian.entities.profil.Keahlian;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KeahlianPostRequest {
    @NotEmpty(message = "Biodata ID is required")
    private String biodataId;
    @Min(value = 1, message = "Keahlian ID is required")
    private Long keahlianId;
    @NotNull(message = "Kualifikasi is required")
    @Enumerated(EnumType.ORDINAL)
    private EKualifikasi kualifikasi;
    private Boolean sertifikasi = false;
    @NotEmpty(message = "Institusi is required")
    private String institusi;
    @Min(value = 1970, message = "Tahun is required")
    private Integer tahun;
    private String masaBerlaku;

    @JsonIgnore
    public Specification<Keahlian> getSpecification() {
        return SpecificationBuilder.<Keahlian>of()
                .addEqual(biodataId, "biodata", "nik")
                .addEqual(keahlianId, "jenisKeahlian", "id")
                .addEqual(tahun, "tahun")
                .build();
    }
}
