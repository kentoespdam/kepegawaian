package id.perumdamts.kepegawaian.dto.master.golongan;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GolonganIndexQuery extends CommonPageRequest {
    private String golongan;
    private String pangkat;
}
