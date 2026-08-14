package id.perumdamts.kepegawaian.dto.kepegawaian.terminasi;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatTerminasi;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

/**
 * DTO dedicated terminasi — TIDAK mewarisi {@code RiwayatSkPostRequest} agar kontrak
 * FE eksplisit: hanya field SK-inti yang relevan (SK pensiun/berhenti) + field terminasi.
 * Field SK-gaji (grup GajiSk: gajiPokok, mkg*, kenaikanBerikutnya, updateMaster) sengaja
 * tidak ada — saga membangun {@code RiwayatSkPostRequest} sendiri di
 * {@code RiwayatTerminasiCommandService} (updateMaster selalu false).
 */
@Data
public class RiwayatTerminasiPostRequest {
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
    @Min(value = 1, message = "Golongan ID must be greater than or equal to 1")
    private Long golonganId;
    private String notes;

    @NotNull(message = "Alasan Berhenti is required")
    @Min(value = 1, message = "Alasan Berhenti is required")
    private Long alasanTerminasiId;
    @NotNull(message = "Nipam is required")
    @NotEmpty(message = "Nipam ID is required")
    private String nipam;
    @NotNull(message = "Nama is required")
    @NotEmpty(message = "Nama is required")
    private String nama;
    @NotNull(message = "Organisasi ID is required")
    @Min(value = 1, message = "Organisasi ID must be greater than or equal to 1")
    private Long organisasiId;
    @NotNull(message = "Jabatan ID is required")
    @Min(value = 1, message = "Jabatan ID must be greater than or equal to 1")
    private Long jabatanId;
    private MultipartFile fileName;

    @JsonIgnore
    public Specification<RiwayatTerminasi> getTerminasiSpecification() {
        return SpecificationBuilder.<RiwayatTerminasi>of()
                .addEqual(getPegawaiId(), "pegawai", "id")
                .addEqual(getNomorSk(), "skTerminasi", "nomorSk")
                .addEqual(getTanggalSk(), "skTerminasi", "tanggalSk")
                .build();
    }


}
