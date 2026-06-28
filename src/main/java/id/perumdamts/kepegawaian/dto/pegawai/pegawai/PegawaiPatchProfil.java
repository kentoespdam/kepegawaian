package id.perumdamts.kepegawaian.dto.pegawai.pegawai;

import id.perumdamts.kepegawaian.entities.commons.EAgama;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PegawaiPatchProfil {
    @NotNull(message = "ID is required")
    @Min(value = 1, message = "ID is required")
    private Long id;
    @NotEmpty(message = "NIPAM is required")
    @NotNull(message = "NIPAM is required")
    private String nipam;
    @NotEmpty(message = "Nama is required")
    @NotNull(message = "Nama is required")
    private String nama;
    private EJenisKelamin jenisKelamin;
    private EStatusKawin statusKawin;
    private EAgama agama;
    private String tempatLahir;
    private LocalDate tanggalLahir;
    private String alamat;
    private String ibuKandung;
    private String telp;
    private Long golonganId;
    private Long organisasiId;
    private Long jabatanId;
    private Long profesiId;
    private String email;
    private Long absensiId;
}
