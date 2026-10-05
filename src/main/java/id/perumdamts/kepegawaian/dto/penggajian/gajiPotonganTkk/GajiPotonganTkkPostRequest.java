package id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk;

import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPotonganTkk;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GajiPotonganTkkPostRequest {
    @NotNull(message = "Pegawai ID is required")
    @Enumerated(EnumType.ORDINAL)
    private EStatusPegawai statusPegawai;
    private Long levelId;
    private Long golonganId;
    private Double nominal;




}
