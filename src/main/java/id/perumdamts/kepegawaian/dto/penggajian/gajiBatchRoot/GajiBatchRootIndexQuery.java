package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiBatchRootIndexQuery extends PagedRequest {
    private String periode;
    private EProsesGaji status;
    private String ltStatus;
    private String gtStatus;
}
