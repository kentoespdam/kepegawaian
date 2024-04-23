package id.perumdamts.kepegawaian.dto.profil.keahlian;

import id.perumdamts.kepegawaian.dto.master.jenisKeahlian.JenisKeahlianResponse;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EKualifikasi;
import id.perumdamts.kepegawaian.entities.profil.Keahlian;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class KeahlianResponse {
    private Long id;
    private BiodataMiniResponse biodata;
    private JenisKeahlianResponse jenisKeahlian;
    @Enumerated(EnumType.ORDINAL)
    private EKualifikasi kualifikasi;
    private Boolean sertifikasi;
    private String institusi;
    private Integer tahun;
    private String masaBerlaku;
    private Boolean disetujui;
    private String disetujuiOleh;

    public static KeahlianResponse from(Keahlian entity) {
        BiodataMiniResponse biodata = BiodataMiniResponse.from(entity.getBiodata());
        JenisKeahlianResponse jenisKeahlian = JenisKeahlianResponse.from(entity.getJenisKeahlian());
        KeahlianResponse response = new KeahlianResponse();
        response.setId(entity.getId());
        response.setBiodata(biodata);
        response.setJenisKeahlian(jenisKeahlian);
        response.setKualifikasi(entity.getKualifikasi());
        response.setSertifikasi(entity.getSertifikasi());
        response.setInstitusi(entity.getInstitusi());
        response.setTahun(entity.getTahun());
        response.setMasaBerlaku(entity.getMasaBerlaku());
        response.setDisetujui(entity.getDisetujui());
        response.setDisetujuiOleh(entity.getDisetujuiOleh());
        return response;
    }
}
