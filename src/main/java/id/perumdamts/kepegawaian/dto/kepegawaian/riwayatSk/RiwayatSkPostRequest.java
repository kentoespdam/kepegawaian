package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RiwayatSkPostRequest {
    @NotNull(message = "Pegawai ID is required")
    @Min(value = 1, message = "Pegawai ID is required")
    private Long pegawaiId;
    @NotEmpty(message = "Nomor SK is required")
    private String nomorSk;
    @Enumerated(EnumType.ORDINAL)
    @NotNull(message = "Jenis SK is required")
    private EJenisSk jenisSk;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Tanggal SK is required")
    private LocalDate tanggalSk;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "TMT Berlaku is required")
    private LocalDate tmtBerlaku;
    @NotNull(message = "Golongan ID is required", groups = GajiSk.class)
    @Min(value = 1, message = "Golongan ID is required", groups = GajiSk.class)
    private Long golonganId;
    @NotNull(message = "Gaji Pokok is required", groups = GajiSk.class)
    private Double gajiPokok;
    @Min(value = 0, message = "MKG Tahun is required", groups = GajiSk.class)
    private Integer mkgTahun;
    @Min(value = 0, message = "MKG Bulan is required", groups = GajiSk.class)
    private Integer mkgBulan;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Kenaikan Berikutnya is required", groups = GajiSk.class)
    private LocalDate kenaikanBerikutnya;
    @JsonSerialize(using = LocalDateSerializer.class)
    private Integer mkgbTahun;
    @JsonSerialize(using = LocalDateSerializer.class)
    private Integer mkgbBulan;
    private Boolean updateMaster = false;
    private String notes;


    @JsonIgnore
    public Specification<RiwayatSk> getSpecification() {
        return SpecificationBuilder.<RiwayatSk>of()
                .addEqual(pegawaiId, "pegawai", "id")
                .addEqual(nomorSk, "nomorSk")
                .addEqual(jenisSk, "jenisSk")
                .addEqual(tanggalSk, "tanggalSk")
                .build();
    }

}
