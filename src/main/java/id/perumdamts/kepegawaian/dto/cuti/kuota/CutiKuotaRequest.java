package id.perumdamts.kepegawaian.dto.cuti.kuota;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.cuti.CutiKuota;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class CutiKuotaRequest extends CommonPageRequest {
    public Long pegawaiId;
    public Integer tahun = LocalDate.now().getYear();
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expired;

    @JsonIgnore
    public Specification<Pegawai> getPegawaiSpecification() {
        log.info("tahun: {}", tahun);
        Specification<Pegawai> pegawaiSpec = Objects.isNull(pegawaiId) ? null :
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("id"), pegawaiId);
        Specification<Pegawai> statusKerjaSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.in(root.get("statusKerja")).value(List.of(EStatusKerja.KARYAWAN_AKTIF, EStatusKerja.DIRUMAHKAN));
        return Specification.where(pegawaiSpec).and(statusKerjaSpec);
    }

    @JsonIgnore
    public Specification<CutiKuota> getSpecification() {
        Specification<CutiKuota> pegawaiSpec = Objects.isNull(pegawaiId) ? null :
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("pegawai").get("id"), pegawaiId);
        Specification<CutiKuota> tahunSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("tahun"), tahun);
        Specification<CutiKuota> expiredSpec = Objects.isNull(expired) ? null :
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("expired"), expired);
        return Specification.where(pegawaiSpec).and(tahunSpec).and(expiredSpec);
    }

    @JsonIgnore
    public Specification<CutiKuota> getSpecificationFromPegawai(List<Long> pegawaiIdList) {
        List<Integer> tahunList = new ArrayList<>();
        for (int i = tahun; i >= tahun - 2; i--) tahunList.add(i);
        Specification<CutiKuota> pegawaiSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.in(root.get("pegawai").get("id")).value(pegawaiIdList);
        Specification<CutiKuota> tahunSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.in(root.get("tahun")).value(tahunList);
        Specification<CutiKuota> expiredSpec = Objects.isNull(expired) ? null :
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("expired"), expired);
        return Specification.where(pegawaiSpec).and(tahunSpec).and(expiredSpec);
    }
}
