package id.perumdamts.kepegawaian.dto.profil.keluarga;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanMiniResponse;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.*;
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
    private EStatusPendidikan statusPendidikan;
    @Enumerated
    private EStatusKawin statusKawin;
    private String notes;

    public static ProfilKeluargaResponse from(ProfilKeluarga entity) {
        ProfilKeluargaResponse response = new ProfilKeluargaResponse();
        response.setId(entity.getId());
        response.setBiodata(BiodataMiniResponse.from(entity.getBiodata()));
        response.setNik(entity.getNik());
        response.setNama(entity.getNama());
        response.setJenisKelamin(entity.getJenisKelamin());
        response.setAgama(entity.getAgama());
        response.setHubunganKeluarga(entity.getHubunganKeluarga());
        response.setTempatLahir(entity.getTempatLahir());
        response.setTanggalLahir(entity.getTanggalLahir());
        response.setTanggungan(entity.getTanggungan());
        if (entity.getPendidikan() != null)
            response.setPendidikan(JenjangPendidikanMiniResponse.from(entity.getPendidikan()));
        response.setStatusPendidikan(entity.getStatusPendidikan());
        response.setStatusKawin(entity.getStatusKawin());
        response.setNotes(entity.getNotes());
        return response;
    }
}
