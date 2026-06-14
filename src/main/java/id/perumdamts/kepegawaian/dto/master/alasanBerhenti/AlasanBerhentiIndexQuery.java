package id.perumdamts.kepegawaian.dto.master.alasanBerhenti;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AlasanBerhentiIndexQuery extends CommonPageRequest {
    private String nama;
}
