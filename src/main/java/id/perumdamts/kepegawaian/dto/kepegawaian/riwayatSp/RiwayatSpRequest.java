package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSp;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class RiwayatSpRequest extends CommonPageRequest {
    private Long pegawaiId;
    private String nomorSp;
    private Long jenisSpId;

    @JsonIgnore
    public Specification<RiwayatSp> getSpecification() {
        Specification<RiwayatSp> nomorSpSpec = Objects.isNull(nomorSp) ? null : (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("nomorSp"), nomorSp);
        Specification<RiwayatSp> pegawaiIdSpec = Objects.isNull(pegawaiId) ? null : (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("pegawai").get("id"), pegawaiId);
        Specification<RiwayatSp> jenisSpIdSpec = Objects.isNull(jenisSpId) ? null : (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("jenisSp").get("id"), jenisSpId);
        return Specification.where(nomorSpSpec).and(pegawaiIdSpec).and(jenisSpIdSpec);
    }
}
