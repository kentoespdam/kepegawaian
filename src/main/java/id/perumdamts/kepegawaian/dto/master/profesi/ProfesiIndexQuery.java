package id.perumdamts.kepegawaian.dto.master.profesi;

import id.perumdamts.kepegawaian.dto.master.profesi.commons.PagedRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProfesiIndexQuery extends PagedRequest {
    private Long organisasiId;
    private Long jabatanId;
    private Long levelId;
    private Long gradeId;
    private String nama;
}
