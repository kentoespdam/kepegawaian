package id.perumdamts.kepegawaian.dto.profil.pendidikan;

import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanResponse;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataMiniResponse;
import id.perumdamts.kepegawaian.entities.profil.Pendidikan;
import lombok.Data;

@Data
public class PendidikanResponse {
    private Long id;
    private BiodataMiniResponse biodata;
    private JenjangPendidikanResponse jenjangPendidikan;
    private String gelarDepan;
    private String gelarBelakang;
    private String jurusan;
    private String institusi;
    private String kota;
    private Integer tahunMasuk;
    private Boolean isLulus;
    private Integer tahunLulus;
    private Double gpa;
    private Boolean isLatest;
    private Boolean changedStatus;


    public static PendidikanResponse from(Pendidikan entity) {
        BiodataMiniResponse biodata = BiodataMiniResponse.from(entity.getBiodata());
        JenjangPendidikanResponse jenjangPendidikan = JenjangPendidikanResponse.from(entity.getJenjangPendidikan());
        PendidikanResponse response = new PendidikanResponse();
        response.setId(entity.getId());
        response.setBiodata(biodata);
        response.setJenjangPendidikan(jenjangPendidikan);
        response.setGelarDepan(entity.getGelarDepan());
        response.setGelarBelakang(entity.getGelarBelakang());
        response.setJurusan(entity.getJurusan());
        response.setInstitusi(entity.getInstitusi());
        response.setKota(entity.getKota());
        response.setTahunMasuk(entity.getTahunMasuk());
        response.setIsLulus(entity.getIsLulus());
        response.setTahunLulus(entity.getTahunLulus());
        response.setGpa(entity.getGpa());
        response.setIsLatest(entity.getIsLatest());
        response.setChangedStatus(entity.getChangedStatus());
        return response;
    }
}
