package id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen;

import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiKomponen;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GajiKomponenPostRequest {
    private Integer urut;
    @Min(value = 1, message = "Profil Gaji ID is required")
    @NotNull(message = "Profil Gaji ID is required")
    private Long profilGajiId;
    @NotEmpty(message = "Kode is required")
    @NotNull(message = "Kode is required")
    private String kode;
    @NotEmpty(message = "Nama is required")
    @NotNull(message = "Nama is required")
    private String nama;
    @Enumerated(EnumType.STRING)
    private EJenisGaji jenisGaji = EJenisGaji.NONE;
    private Double nilai;
    private Boolean isReference = false;
    private String formula;



}
