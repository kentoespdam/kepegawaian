package id.perumdamts.kepegawaian.dto.master.sanksi;

import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpMiniResponse;
import lombok.Data;

@Data
public class SanksiQuery {
    private Long id;
    private String kode;
    private String keterangan;
    private Long jenisSpId;
    private JenisSpMiniResponse jenisSp;
    private Boolean potTkk;
    private Integer jmlPotTkk;
    private Boolean isPendingPangkat;
    private Boolean isPendingGaji;
    private Boolean isTurunPangkat;
    private Boolean isTurunJabatan;
    private Boolean isSuspension;
    private Boolean isTerminateDh;
    private Boolean isTerminateTh;
}
