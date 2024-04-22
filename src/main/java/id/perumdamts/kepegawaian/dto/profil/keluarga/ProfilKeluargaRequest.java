package id.perumdamts.kepegawaian.dto.profil.keluarga;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EHubunganKeluarga;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.profil.ProfilKeluarga;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProfilKeluargaRequest extends CommonPageRequest {
    private String biodataId;
    private String nik;
    private String nama;
    @Enumerated(EnumType.ORDINAL)
    private EHubunganKeluarga hubunganKeluarga;
    private Boolean tanggungan;
    @Enumerated(EnumType.ORDINAL)
    private EStatusKawin statusKawin;

    @JsonIgnore
    public Specification<ProfilKeluarga> getSpecification() {
        Specification<ProfilKeluarga> biodataSpec = Objects.isNull(biodataId) ? null :
                (root, query, cb) -> cb.equal(root.get("biodata").get("nik"), biodataId);
        Specification<ProfilKeluarga> nikSpec = Objects.isNull(nik) ? null :
                (root, query, cb) -> cb.equal(root.get("nik"), nik);
        Specification<ProfilKeluarga> namaSpec = Objects.isNull(nama) ? null :
                (root, query, cb) -> cb.like(root.get("nama"), "%" + nama + "%");
        Specification<ProfilKeluarga> hubunganSpec = Objects.isNull(hubunganKeluarga) ? null :
                (root, query, cb) -> cb.equal(root.get("hubunganKeluarga"), hubunganKeluarga);
        Specification<ProfilKeluarga> tanggunganSpec = Objects.isNull(tanggungan) ? null :
                (root, query, cb) -> cb.equal(root.get("tanggungan"), tanggungan);
        Specification<ProfilKeluarga> statusSpec = Objects.isNull(statusKawin) ? null :
                (root, query, cb) -> cb.equal(root.get("statusKawin"), statusKawin);
        return Specification.where(biodataSpec).and(nikSpec).and(namaSpec)
                .and(hubunganSpec).and(tanggunganSpec).and(statusSpec);
    }
}
