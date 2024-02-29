package id.perumdamts.kepegawaian.dto.master.statusPegawai;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.StatusPegawai;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class StatusPegawaiRequest extends CommonPageRequest {
    private String nama;

    @JsonIgnore
    public Specification<StatusPegawai> getSpecification() {
        Specification<StatusPegawai> namaSpec = Objects.isNull(nama) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("nama"), nama);

        return Specification.where(namaSpec);
    }
}
