package id.perumdamts.kepegawaian.dto.profil.pendidikan;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.profil.Pendidikan;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class PendidikanRequest extends CommonPageRequest {
    private String biodataId;
    private Long jenjangId;
    private String gelarDepan;
    private String gelarBelakang;
    private String jurusan;
    private String institusi;
    private String kota;
    private Integer tahunMasuk;
    private Integer tahunLulus;
    private Double gpa;
    private Boolean isLatest;

    public Specification<Pendidikan> getSpecification() {
        return SpecificationBuilder.<Pendidikan>of()
                .addEqual(biodataId, "biodata", "nik")
                .addEqual(jenjangId, "jenjangPendidikan", "id")
                .addLike(gelarDepan, "gelarDepan")
                .addLike(gelarBelakang, "gelarBelakang")
                .addLike(jurusan, "jurusan")
                .addLike(institusi, "institusi")
                .addLike(kota, "kota")
                .addEqual(tahunMasuk, "tahunMasuk")
                .addEqual(tahunLulus, "tahunLulus")
                .addEqual(gpa, "gpa")
                .addEqual(isLatest, "isLatest")
                .build();

    }
}
