package id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiPotonganTkkIndexQuery extends PagedRequest {
    private EStatusPegawai statusPegawai;
    private Long levelId;
    private Long golonganId;
}
