package id.perumdamts.kepegawaian.dto.profil.kartuIdentitas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.dto.master.jenisKitas.JenisKitasResponse;
import id.perumdamts.kepegawaian.entities.profil.KartuIdentitas;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class KartuIdentitasRequest extends CommonPageRequest {
    private String nik;
    private JenisKitasResponse jenisKartu;
    private String nomorKartu;

    @JsonIgnore
    public Specification<KartuIdentitas> getSpecification() {
        Specification<KartuIdentitas> nikSpec = Objects.isNull(nik) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("biodata").get("nik"), nik);
        Specification<KartuIdentitas> nomorKartuSpec = Objects.isNull(nomorKartu) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("nomorKartu"), nomorKartu);
        return Specification.where(nikSpec).and(nomorKartuSpec);
    }
}
