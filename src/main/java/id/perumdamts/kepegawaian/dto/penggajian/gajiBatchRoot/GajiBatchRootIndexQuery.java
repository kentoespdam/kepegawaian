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

    @Override
    public String getSortDirection() {
        // Default sort = id desc (newest batch first); direction only applies once a column is chosen
        if ((sortBy == null || sortBy.isBlank()) && "asc".equals(sortDirection)) {
            return "desc";
        }
        return sortDirection;
    }
}
