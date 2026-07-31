package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk;

import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import lombok.Data;

@Data
public class RiwayatSkListRequest {
    private Long pegawaiId;
    private String nomorSk;
    private EJenisSk jenisSk;
    private Long golonganId;
}
