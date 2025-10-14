package id.perumdamts.kepegawaian.dto.master.hariLibur;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisLibur;
import id.perumdamts.kepegawaian.entities.master.HariLibur;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.criteria.Expression;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class HariLiburRequest extends CommonPageRequest {
    private Integer tahun = LocalDate.now().getYear();
    private Integer bulan;
    @Enumerated(EnumType.ORDINAL)
    private EJenisLibur jenisLibur;

    public Specification<HariLibur> getSpecification() {
        SpecificationBuilder<HariLibur> builder = SpecificationBuilder.<HariLibur>of()
                .addEqual(jenisLibur, "jenisLibur");

        if (Objects.nonNull(tahun))
            builder.addCustom((root, cb) -> {
                Expression<Integer> function = cb.function("YEAR", Integer.class, root.get("tanggal"));
                return cb.equal(function, tahun);
            });
        if (Objects.nonNull(bulan))
            builder.addCustom((root, cb) -> {
                Expression<Integer> function = cb.function("MONTH", Integer.class, root.get("tanggal"));
                return cb.equal(function, bulan);
            });
        return builder.build();
    }
}
