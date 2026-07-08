package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.commons.EJenisKontrak;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatKontrak;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@Data
public class RiwayatKontrakPostRequest {
    private EJenisKontrak jenisKontrak;
    @NotNull(message = "Pegawai ID is required")
    @Min(value = 1, message = "Pegawai is required")
    private Long pegawaiId;
    @NotEmpty(message = "NIPAM is required")
    private String nipam;
    @NotEmpty(message = "Nama is required")
    private String nama;
    @NotEmpty(message = "Nomor Kontrak is required")
    private String nomorKontrak;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Tanggal SK is required")
    private LocalDate tanggalSk;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Tanggal Mulai is required")
    private LocalDate tanggalMulai;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Tanggal Selesai is required", groups = PerpanjanganKontrak.class)
    private LocalDate tanggalSelesai;
    @NotNull(message = "Golongan ID is required")
    @Min(value = 1, message = "Golongan is required", groups = KontrakToCapeg.class)
    private Long golonganId;
    @Min(value = 0, message = "Gaji Pokok must be greater than or equal to 0")
    private Double gajiPokok;
    private Boolean isLatest = false;
    private String notes;

    @JsonIgnore
    public Specification<RiwayatKontrak> getSpecification() {
        return SpecificationBuilder.<RiwayatKontrak>of()
                .addEqual(pegawaiId, "pegawai", "id")
                .addEqual(nipam, "nipam")
                .addEqual(nomorKontrak, "nomorKontrak")
                .addEqual(jenisKontrak, "jenisKontrak")
                .build();
    }
}
