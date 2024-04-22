package id.perumdamts.kepegawaian.dto.profil.keluarga;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanMiniResponse;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EAgama;
import id.perumdamts.kepegawaian.entities.commons.EHubunganKeluarga;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.profil.ProfilKeluarga;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfilKeluargaResponse {
    private Long id;
    private BiodataMiniResponse biodata;
    private String nik;
    private String nama;
    @Enumerated
    private EJenisKelamin jenisKelamin;
    @Enumerated
    private EAgama agama;
    @Enumerated
    private EHubunganKeluarga hubunganKeluarga;
    private String tempatLahir;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalLahir;
    private Boolean tanggungan;
    private JenjangPendidikanMiniResponse pendidikan;
    @Enumerated
    private EStatusKawin statusKawin;
    private String notes;

    public static ProfilKeluargaResponse from(ProfilKeluarga profilKeluarga) {
        BiodataMiniResponse biodata = BiodataMiniResponse.from(profilKeluarga.getBiodata());
        JenjangPendidikanMiniResponse pendidikan = JenjangPendidikanMiniResponse.from(profilKeluarga.getPendidikan());
        ProfilKeluargaResponse response = new ProfilKeluargaResponse();
        response.setId(profilKeluarga.getId());
        response.setBiodata(biodata);
        response.setNik(profilKeluarga.getNik());
        response.setNama(profilKeluarga.getNama());
        response.setJenisKelamin(profilKeluarga.getJenisKelamin());
        response.setAgama(profilKeluarga.getAgama());
        response.setHubunganKeluarga(profilKeluarga.getHubunganKeluarga());
        response.setTempatLahir(profilKeluarga.getTempatLahir());
        response.setTanggalLahir(profilKeluarga.getTanggalLahir());
        response.setTanggungan(profilKeluarga.getTanggungan());
        response.setPendidikan(pendidikan);
        response.setStatusKawin(profilKeluarga.getStatusKawin());
        response.setNotes(profilKeluarga.getNotes());
        return response;
    }
}
