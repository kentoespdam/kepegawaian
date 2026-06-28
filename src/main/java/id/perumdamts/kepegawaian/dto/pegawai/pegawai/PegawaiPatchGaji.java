package id.perumdamts.kepegawaian.dto.pegawai.pegawai;

import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PegawaiPatchGaji {
    private LocalDate tmtKerja;
    private LocalDate tmtPensiun;
    @NotNull(message = "Status Pegawai is required")
    @Enumerated(EnumType.ORDINAL)
    private EStatusPegawai statusPegawai;
    private Double gajiPokok;
    private Double phdp;
    private Boolean isAskes;
    @Min(value = 1, message = "Kode Pajak is required")
    @NotNull(message = "Kode Pajak is required")
    private Long kodePajakId;
    @Min(value = 1, message = "Gaji Profil ID is required")
    @NotNull(message = "Gaji Profil ID is required")
    private Long gajiProfilId;
    private Long rumahDinasId;
}
