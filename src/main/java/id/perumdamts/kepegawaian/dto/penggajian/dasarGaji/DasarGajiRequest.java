package id.perumdamts.kepegawaian.dto.penggajian.dasarGaji;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.penggajian.DasarGaji;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class DasarGajiRequest extends CommonPageRequest {
    private String deskripsi;
    private LocalDate tanggalAwal;
    private LocalDate tanggalAkhir;
    private Boolean aktif;

    @JsonIgnore
    public Specification<DasarGaji> getSpecification() {
        return SpecificationBuilder.<DasarGaji>of()
                .addLike(deskripsi, "deskripsi")
                .addGreaterThan(tanggalAwal, "tanggalAwal")
                .addLessThanOrEqual(tanggalAkhir, "tanggalAkhir")
                .addEqual(aktif, "aktif")
                .build();
    }
}
