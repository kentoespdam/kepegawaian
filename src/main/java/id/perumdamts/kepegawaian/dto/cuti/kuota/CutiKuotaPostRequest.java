package id.perumdamts.kepegawaian.dto.cuti.kuota;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.cuti.CutiKuota;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@Data
public class CutiKuotaPostRequest {
    @NotNull(message = "Pegawai id is required")
    @Min(value = 1, message = "Pegawai id is required")
    private Long pegawaiId;
    @NotNull(message = "Tahun is required")
    @Min(value = 2000, message = "Tahun is required")
    private Integer tahun;
    private Integer kuota = 0;
    private Integer kuotaTambahan = 0;
    private Integer sisaKuota = 0;
    @NotNull(message = "Expired is required")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expired;

    @JsonIgnore
    public Specification<CutiKuota> getSpecification() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get("pegawai").get("id"), pegawaiId),
                criteriaBuilder.equal(root.get("tahun"), tahun)
        );
    }

    public static CutiKuota toEntity(CutiKuotaPostRequest request, Pegawai pegawai) {
        CutiKuota entity = new CutiKuota();
        entity.setPegawai(pegawai);
        entity.setTahun(request.getTahun());
        entity.setKuota(request.getKuota());
        entity.setKuotaTambahan(request.getKuotaTambahan());
        entity.setSisaKuota(request.getSisaKuota());
        entity.setExpired(request.getExpired());
        return entity;
    }
}
