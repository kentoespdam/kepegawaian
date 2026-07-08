package id.perumdamts.kepegawaian.dto.kepegawaian.terminasi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkPostRequest;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatTerminasi;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

@EqualsAndHashCode(callSuper = true)
@Data
public class RiwayatTerminasiPostRequest extends RiwayatSkPostRequest {
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
                .build();
    }


}
