package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses;

import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMasterProses;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class GajiBatchMasterProsesResponse {
    private Long id;
    private Long gajiBatchMasterId;
    private Integer urut;
    private String kode;
    private String nama;
    @Enumerated(EnumType.STRING)
    private EJenisGaji jenisGaji;
    private Double nilai;
    private String formula;
    private String nilaiFormula;

    public static GajiBatchMasterProsesResponse from(GajiBatchMasterProses entity) {
        GajiBatchMasterProsesResponse response = new GajiBatchMasterProsesResponse();
        response.setId(entity.getId());
        response.setGajiBatchMasterId(entity.getGajiBatchMaster().getId());
        response.setUrut(entity.getUrut());
        response.setKode(entity.getKode());
        response.setNama(entity.getNama());
        response.setJenisGaji(entity.getJenisGaji());
        response.setNilai(entity.getNilai());
        response.setFormula(entity.getFormula());
        response.setNilaiFormula(entity.getNilaiFormula());
        return response;
    }
}
