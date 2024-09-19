package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatKontrak;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class RiwayatKontrakRequest extends CommonPageRequest {
    private Long pegawaiId;
    private Long riwayatSkId;
    private String nomorSk;

    @JsonIgnore
    public Specification<RiwayatKontrak> getSpecification() {
        Specification<RiwayatKontrak> pegawaiSpec = Objects.isNull(pegawaiId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("pegawai").get("id"), pegawaiId);
        Specification<RiwayatKontrak> riwayatSkSpec = Objects.isNull(riwayatSkId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("riwayatSk").get("id"), riwayatSkId);
        Specification<RiwayatKontrak> skSpec = Objects.isNull(nomorSk) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("riwayatSk").get("nomorSk"), nomorSk);
        return Specification.where(pegawaiSpec).and(riwayatSkSpec).and(skSpec);
    }
}
