package id.perumdamts.kepegawaian.dto.profil.keluarga;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanResponse;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfilKeluargaQuery {
    private Long id;
    private String biodataId;
    private String biodataNik;
    private String biodataNama;
    private String nik;
    private String nama;
    private String jenisKelamin;
    private String agama;
    private String hubunganKeluarga;
    private String tempatLahir;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalLahir;
    private Boolean tanggungan;
    private Long pendidikanId;
    private JenjangPendidikanResponse jenjangPendidikan;
    private String statusPendidikan;
    private Boolean statusKawin;
    private String notes;
    private Integer version;
    private Boolean isDeleted;
    private Boolean changedStatus;
}