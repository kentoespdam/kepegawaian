package id.perumdamts.kepegawaian.dto.profil.pendidikan;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PendidikanIndexQuery extends PagedRequest {
    private String biodataId;
    private Long jenjangId;
    private String gelarDepan;
    private String gelarBelakang;
    private String jurusan;
    private String institusi;
    private String kota;
    private Integer tahunMasuk;
    private Integer tahunLulus;
    private Double gpa;
    private Boolean isLatest;
}
