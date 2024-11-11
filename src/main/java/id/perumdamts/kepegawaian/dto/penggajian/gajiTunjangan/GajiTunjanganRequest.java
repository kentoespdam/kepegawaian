package id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisTunjangan;
import id.perumdamts.kepegawaian.entities.penggajian.GajiTunjangan;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

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
        Specification<GajiTunjangan> jenisSpec = Objects.isNull(jenis) ? null :
                (root, query, cb) -> cb.equal(root.get("jenisTunjangan"), jenis);
        Specification<GajiTunjangan> levelSpec = Objects.isNull(levelId) ? null :
                (root, query, cb) -> cb.equal(root.get("level").get("id"), levelId);
        Specification<GajiTunjangan> golonganSpec = Objects.isNull(golonganId) ? null :
                (root, query, cb) -> cb.equal(root.get("golongan").get("id"), golonganId);
        return Specification.where(jenisSpec).and(levelSpec).and(golonganSpec);
    }
}
