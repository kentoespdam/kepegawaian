package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatKontrak;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class RiwayatKontrakRequest extends CommonPageRequest {
    private Long pegawaiId;
    private String nomorKontrak;

    @JsonIgnore
    public Specification<RiwayatKontrak> getSpecification() {
        return SpecificationBuilder.<RiwayatKontrak>of()
                .addEqual(pegawaiId, "pegawai", "id")
                .addLike(nomorKontrak, "nomorKontrak")
                .build();
    }
}
