package id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan;

import id.perumdamts.kepegawaian.entities.commons.EJenisTunjangan;
import id.perumdamts.kepegawaian.entities.penggajian.GajiTunjangan;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class GajiTunjanganPostRequest {
    @Enumerated(EnumType.ORDINAL)
    private EJenisTunjangan jenisTunjangan;
    private Long levelId;
    private Long golonganId;
    private Double nominal;



}
