package id.perumdamts.kepegawaian.dto.penggajian.dasarGaji;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.penggajian.DasarGaji;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class DasarGajiRequest extends CommonPageRequest {
    private String deskripsi;
    private LocalDate tanggalAwal;
    private LocalDate tanggalAkhir;
    private Boolean aktif;

    @JsonIgnore
    public Specification<DasarGaji> getSpecification() {
        Specification<DasarGaji> deskripsiSpec = Objects.isNull(deskripsi) ? null :
                (root, query, cb) -> cb.like(root.get("deskripsi"), "%" + deskripsi + "%");
        Specification<DasarGaji> tglAwalSpec = Objects.isNull(tanggalAwal) ? null :
                (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("tglAwal"), tanggalAwal);
        Specification<DasarGaji> tglAkhirSpec = Objects.isNull(tanggalAkhir) ? null :
                (root, query, cb) -> cb.lessThanOrEqualTo(root.get("tglAkhir"), tanggalAkhir);
        Specification<DasarGaji> aktifSpec = Objects.isNull(aktif) ? null :
                (root, query, cb) -> cb.equal(root.get("aktif"), aktif);
        return Specification.where(deskripsiSpec).and(tglAwalSpec)
                .and(tglAkhirSpec).and(aktifSpec);
    }
}
