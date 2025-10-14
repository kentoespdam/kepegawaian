package id.perumdamts.kepegawaian.dto.cuti.kuota;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.cuti.CutiKuota;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

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
        return SpecificationBuilder.<CutiKuota>of()
                .addEqual(pegawaiId, "pegawai", "id")
                .addLike(nipam, "pegawai", "nipam")
                .addLike(nama, "pegawai", "biodata", "nama")
                .addEqual(tahun, "tahun")
                .addEqual(expired, "expired")
                .build();
    }
}
