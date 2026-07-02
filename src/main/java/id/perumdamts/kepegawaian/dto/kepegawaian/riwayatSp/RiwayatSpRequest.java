package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RiwayatSpRequest extends PagedRequest {
    private Long pegawaiId;
    private String nomorSp;
    private Long jenisSpId;
}
