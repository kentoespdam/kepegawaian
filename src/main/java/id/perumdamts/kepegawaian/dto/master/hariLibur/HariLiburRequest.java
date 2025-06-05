package id.perumdamts.kepegawaian.dto.master.hariLibur;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisLibur;
import id.perumdamts.kepegawaian.entities.master.HariLibur;
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
        Specification<HariLibur> tahunSpec = (root, query, cb) -> {
            Expression<Integer> function = cb.function("YEAR", Integer.class, root.get("tanggal"));
            return cb.equal(function, tahun);
        };
        Specification<HariLibur> bulanSpec = Objects.isNull(bulan) ? null :
                (root, query, cb) -> {
                    Expression<Integer> function = cb.function("MONTH", Integer.class, root.get("tanggal"));
                    return cb.equal(function, bulan);
                };
        Specification<HariLibur> jenisLiburSpec = Objects.isNull(jenisLibur) ? null :
                (root, query, cb) -> cb.equal(root.get("jenisLibur"), jenisLibur);
        return Specification.where(tahunSpec).and(bulanSpec).and(jenisLiburSpec);
    }
}
