package id.perumdamts.kepegawaian.dto.profil.pendidikan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanResponse;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataMiniResponse;
import id.perumdamts.kepegawaian.entities.profil.Pendidikan;
import lombok.Data;

import java.time.LocalDateTime;

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
    private Integer tahunLulus;
    private Double gpa;
    private Boolean isLatest;
    private Boolean disetujui;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tanggalPengajuan;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tanggalDisetujui;
    private String disetujuiOleh;

    public  static PendidikanResponse from(Pendidikan entity) {
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
        response.setTahunLulus(entity.getTahunLulus());
        response.setGpa(entity.getGpa());
        response.setIsLatest(entity.getIsLatest());
        response.setTanggalPengajuan(entity.getTanggalPengajuan());
        response.setDisetujui(entity.getDisetujui());
        response.setDisetujuiOleh(entity.getDisetujuiOleh());
        return response;
    }
}
