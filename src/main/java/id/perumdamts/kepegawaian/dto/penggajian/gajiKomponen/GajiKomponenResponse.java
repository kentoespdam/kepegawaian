package id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen;

import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiKomponen;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class GajiKomponenResponse {
    private Long id;
    private GajiProfilResponse profilGaji;
    private String kode;
    private String nama;
    @Enumerated(EnumType.STRING)
    private EJenisGaji jenisGaji;
    private Double nilai;
    private Boolean isReference = false;
    private String formula;

    public static GajiKomponenResponse from(GajiKomponen gajiKomponen) {
        GajiKomponenResponse response = new GajiKomponenResponse();
        response.setId(gajiKomponen.getId());
        response.setProfilGaji(GajiProfilResponse.from(gajiKomponen.getProfilGaji()));
        response.setKode(gajiKomponen.getKode());
        response.setNama(gajiKomponen.getNama());
        response.setJenisGaji(gajiKomponen.getJenisGaji());
        response.setNilai(gajiKomponen.getNilai());
        response.setIsReference(gajiKomponen.getIsReference());
        response.setFormula(gajiKomponen.getFormula());
        return response;
    }
}
