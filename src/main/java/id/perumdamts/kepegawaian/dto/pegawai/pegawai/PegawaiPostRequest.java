package id.perumdamts.kepegawaian.dto.pegawai.pegawai;

import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class PegawaiPostRequest extends BiodataPostRequest {
    @NotEmpty(message = "Nipam is required")
    private String nipam;
    @Enumerated(EnumType.ORDINAL)
    private EStatusPegawai statusPegawai;
    @Enumerated(EnumType.ORDINAL)
    private EStatusKerja statusKerja;
    @NotNull(message = "Jabatan is required")
    @Min(value = 1, message = "Jabatan is required")
    private Long jabatanId;
    @NotNull(message = "Organisasi is required")
    @Min(value = 1, message = "Organisasi is required")
    private Long organisasiId;
    private Long profesiId;
    @NotNull(message = "Golongan is required", groups = PegawaiTetap.class)
    @Min(value = 1, message = "Golongan is required", groups = PegawaiTetap.class)
    private Long golonganId;
    @NotNull(message = "Kode Pajak is required")
    @Min(value = 1, message = "Kode Pajak is required")
    private Long kodePajakId;
    private String nomorSk;
    private LocalDate tanggalSk;
    private LocalDate tmtBerlakuSk;
    private LocalDate tmtKontrakSelesai;
    private Double gajiPokok;
    private String email;
    private String notes;
}
