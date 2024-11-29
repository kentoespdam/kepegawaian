package id.perumdamts.kepegawaian.dto.pegawai;

import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.master.RumahDinas;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPendapatanNonPajak;
import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PegawaiPatchGaji {
    @NotNull(message = "Status Pegawai is required")
    @Enumerated(EnumType.ORDINAL)
    private EStatusPegawai statusPegawai;
    private Double gajiPokok;
    private Boolean isAskes;
    @NotEmpty(message = "Kode Pajak is required")
    @NotNull(message = "Kode Pajak is required")
    private Long kodePajakId;
    @Min(value = 1, message = "Gaji Profil ID is required")
    @NotNull(message = "Gaji Profil ID is required")
    private Long gajiProfilId;
    private Long rumahDinasId;

    public static Pegawai toEntity(Pegawai entity, PegawaiPatchGaji request, GajiPendapatanNonPajak kodePajak, GajiProfil gajiProfil, RumahDinas rumahDinas) {
        entity.setStatusPegawai(request.getStatusPegawai());
        entity.setGajiPokok(request.getGajiPokok());
        entity.setIsAskes(request.getIsAskes());
        entity.setKodePajak(kodePajak);
        entity.setGajiProfil(gajiProfil);
        entity.setRumahDinas(rumahDinas);
        return entity;
    }
}
