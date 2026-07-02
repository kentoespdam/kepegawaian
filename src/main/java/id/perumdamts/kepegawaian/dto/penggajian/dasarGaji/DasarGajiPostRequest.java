package id.perumdamts.kepegawaian.dto.penggajian.dasarGaji;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.penggajian.DasarGaji;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@Data
public class DasarGajiPostRequest {
    @NotEmpty(message = "Deskripsi is required")
    private String deskripsi;
    @NotNull(message = "Tanggal Awal is required")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalAwal;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalAkhir;
    @NotNull(message = "Aktif is required")
    private Boolean aktif;

    @JsonIgnore
    public Specification<DasarGaji> getSpecification() {
        return SpecificationBuilder.<DasarGaji>of()
                .addEqual(deskripsi, "deskripsi")
                .addEqual(tanggalAwal, "tanggalAwal")
                .build();
    }

}
