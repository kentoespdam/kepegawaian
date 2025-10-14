package id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisTunjangan;
import id.perumdamts.kepegawaian.entities.penggajian.GajiTunjangan;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class GajiTunjanganRequest extends CommonPageRequest {
    @Enumerated(EnumType.ORDINAL)
    private EJenisTunjangan jenis;
    private Long levelId;
    private Long golonganId;

    @JsonIgnore
    public Specification<GajiTunjangan> getSpecification() {
        return SpecificationBuilder.<GajiTunjangan>of()
                .addEqual(jenis, "jenisTunjangan")
                .addEqual(levelId, "level", "id")
                .addEqual(golonganId, "golongan", "id")
                .build();
    }
}
