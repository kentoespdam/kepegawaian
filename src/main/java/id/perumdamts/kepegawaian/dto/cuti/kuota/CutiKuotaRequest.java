package id.perumdamts.kepegawaian.dto.cuti.kuota;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.cuti.CutiKuota;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class CutiKuotaRequest extends CommonPageRequest {
    private Long pegawaiId;
    private String nipam;
    private String nama;
    private Integer tahun = LocalDate.now().getYear();
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expired;

    @JsonIgnore
    public Specification<CutiKuota> getSpecification() {
        Specification<CutiKuota> pegawaiSpec = Objects.isNull(pegawaiId) ? null :
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("pegawai").get("id"), pegawaiId);
        Specification<CutiKuota> nipamSpec = Objects.isNull(nipam) ? null :
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.like(root.get("pegawai").get("nipam"), nipam + "%");
        Specification<CutiKuota> namaSpec = Objects.isNull(nama) ? null :
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.like(root.get("pegawai").get("biodata").get("nama"), "%" + nama + "%");
        Specification<CutiKuota> tahunSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("tahun"), tahun);
        Specification<CutiKuota> expiredSpec = Objects.isNull(expired) ? null :
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("expired"), expired);
        return Specification.where(pegawaiSpec).and(nipamSpec).and(namaSpec).and(tahunSpec).and(expiredSpec);
    }
}
