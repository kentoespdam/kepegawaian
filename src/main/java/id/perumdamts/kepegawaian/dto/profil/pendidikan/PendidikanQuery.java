package id.perumdamts.kepegawaian.dto.profil.pendidikan;

import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanResponse;
import lombok.Data;

@Data
public class PendidikanQuery {
    private Long id;
    private String biodataId;
    private String biodataNik;
    private String biodataNama;
    private Long jenjangId;
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
    private Byte changedStatus;
}
