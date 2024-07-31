package id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.penggajian.DasarGaji;
import id.perumdamts.kepegawaian.entities.penggajian.DetailDasarGaji;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@Data
public class DetailDasarGajiPostRequest {
    private Long dasarGajiId;
    private Integer mkg;
    private Long golonganId;
    private Integer nominal;

    @JsonIgnore
    public Specification<DetailDasarGaji> getSpecification() {
        Specification<DetailDasarGaji> dasarGajiSpec = Objects.isNull(dasarGajiId) ? null :
                (root, query, cb) -> cb.equal(root.get("dasarGaji").get("id"), dasarGajiId);
        Specification<DetailDasarGaji> mkgSpec = Objects.isNull(mkg) ? null :
                (root, query, cb) -> cb.equal(root.get("mkg"), mkg);
        Specification<DetailDasarGaji> golonganSpec = Objects.isNull(golonganId) ? null :
                (root, query, cb) -> cb.equal(root.get("golongan").get("id"), golonganId);
        Specification<DetailDasarGaji> nominalSpec = Objects.isNull(nominal) ? null :
                (root, query, cb) -> cb.equal(root.get("nominal"), nominal);
        return Specification.where(dasarGajiSpec).and(mkgSpec).and(golonganSpec).and(nominalSpec);
    }

    public static DetailDasarGaji toEntity(DetailDasarGajiPostRequest request, DasarGaji dasarGaji, Golongan golongan) {
        DetailDasarGaji entity = new DetailDasarGaji();
        entity.setDasarGaji(dasarGaji);
        entity.setMkg(request.getMkg());
        entity.setGolongan(golongan);
        entity.setNominal(request.getNominal());
        return entity;
    }
}
