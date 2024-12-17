package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class RiwayatSkRequest extends CommonPageRequest {
    private Long pegawaiId;
    private String nomorSk;
    @Enumerated(EnumType.ORDINAL)
    private EJenisSk jenisSk;
    private Long golonganId;

    @JsonIgnore
    public Specification<RiwayatSk> getSpecification() {
        Specification<RiwayatSk> pegawaiSpec = Objects.isNull(pegawaiId) ? null :
                (root, query, cb) -> cb.equal(root.get("pegawai").get("id"), pegawaiId);
        Specification<RiwayatSk> nomorSkSpec = Objects.isNull(nomorSk) ? null :
                (root, query, cb) -> cb.equal(root.get("nomorSk"), nomorSk);
        Specification<RiwayatSk> jenisSkSpec = Objects.isNull(jenisSk) ? null :
                (root, query, cb) -> cb.equal(root.get("jenisSk"), jenisSk);
        Specification<RiwayatSk> golonganSpec = Objects.isNull(golonganId) ? null :
                (root, query, cb) -> cb.equal(root.get("golongan").get("id"), golonganId);
        return Specification.where(pegawaiSpec).and(nomorSkSpec).and(jenisSkSpec).and(golonganSpec);
    }
}
