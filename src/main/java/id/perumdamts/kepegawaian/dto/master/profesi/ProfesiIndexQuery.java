package id.perumdamts.kepegawaian.dto.master.profesi;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProfesiIndexQuery extends CommonPageRequest {
    private Long organisasiId;
    private Long jabatanId;
    private Long levelId;
    private Long gradeId;
    private String nama;
}
