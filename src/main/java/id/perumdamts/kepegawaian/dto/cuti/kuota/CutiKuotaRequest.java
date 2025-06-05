package id.perumdamts.kepegawaian.dto.cuti.kuota;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.cuti.CutiKuota;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class CutiKuotaRequest extends CommonPageRequest {
    public Long pegawaiId;
    public Integer tahun;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expired;

    @JsonIgnore
    public Specification<CutiKuota> getSpecification() {
        Specification<CutiKuota> pegawaiSpec = Objects.isNull(pegawaiId) ? null :
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("pegawai").get("id"), pegawaiId);
        Specification<CutiKuota> tahunSpec = Objects.isNull(tahun) ? null :
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("tahun"), tahun);
        Specification<CutiKuota> expiredSpec = Objects.isNull(expired) ? null :
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("expired"), expired);
        return Specification.where(pegawaiSpec).and(tahunSpec).and(expiredSpec);
    }
}
