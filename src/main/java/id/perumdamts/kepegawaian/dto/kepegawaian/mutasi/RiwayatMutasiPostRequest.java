package id.perumdamts.kepegawaian.dto.kepegawaian.mutasi;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatMutasi;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class RiwayatMutasiPostRequest extends RiwayatSkPostRequest {
    private String nipam;
    private String nama;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalBerakhir;
    @NotNull(message = "Jenis Mutasi is required")
    private EJenisMutasi jenisMutasi;
    @NotNull(message = "Organisasi ID is required", groups = MutasiJabatan.class)
    @Min(value = 1, message = "Organisasi ID is required", groups = MutasiJabatan.class)
    private Long organisasiId;
    @NotNull(message = "Jabatan ID is required", groups = MutasiJabatan.class)
    @Min(value = 1, message = "Jabatan ID is required", groups = MutasiJabatan.class)
    private Long jabatanId;
    @NotNull(message = "Profesi ID is required", groups = MutasiJabatan.class)
    @Min(value = 1, message = "Profesi ID is required", groups = MutasiJabatan.class)
    private Long profesiId;
    @NotNull(message = "Golongan ID is required", groups = MutasiGolongan.class)
    @Min(value = 1, message = "Golongan ID must be greater than or equal to 1", groups = MutasiGolongan.class)
    private Long golonganId;
    private Long organisasiLamaId;
    private Long jabatanLamaId;
    private Long golonganLamaId;
    private Long profesiLamaId;
    private String notes;

    @JsonIgnore
    public Specification<RiwayatMutasi> getSpecificationMutasi() {
        return SpecificationBuilder.<RiwayatMutasi>of()
                .addEqual(getNomorSk(), "riwayatSk", "nomorSk")
                .addEqual(getPegawaiId(), "pegawai", "id")
                .addEqual(getTanggalSk(), "riwayatSk", "tanggalSk")
                .build();
    }

}
