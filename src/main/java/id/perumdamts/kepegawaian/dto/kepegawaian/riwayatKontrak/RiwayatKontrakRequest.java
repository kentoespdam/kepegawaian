package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RiwayatKontrakRequest extends PagedRequest {
    private Long pegawaiId;
    private String nomorKontrak;
}
