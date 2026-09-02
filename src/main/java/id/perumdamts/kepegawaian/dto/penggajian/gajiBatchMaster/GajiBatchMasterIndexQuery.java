package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiBatchMasterIndexQuery extends PagedRequest {
    @NotBlank(message = "periode is required")
    private String periode;
    private String search;
}
