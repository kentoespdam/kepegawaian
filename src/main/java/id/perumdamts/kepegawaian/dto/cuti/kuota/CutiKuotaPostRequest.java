package id.perumdamts.kepegawaian.dto.cuti.kuota;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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
}
