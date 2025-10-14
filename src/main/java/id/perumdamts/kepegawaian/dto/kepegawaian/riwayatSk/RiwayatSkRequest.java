package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

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
        return SpecificationBuilder.<RiwayatSk>of()
                .addEqual(pegawaiId, "pegawai", "id")
                .addEqual(nomorSk, "nomorSk")
                .addEqual(jenisSk, "jenisSk")
                .addEqual(golonganId, "golongan", "id")
                .build();
    }
}
