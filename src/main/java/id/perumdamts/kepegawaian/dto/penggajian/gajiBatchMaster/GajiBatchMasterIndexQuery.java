package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiBatchMasterIndexQuery extends PagedRequest {
    private String search;
}
