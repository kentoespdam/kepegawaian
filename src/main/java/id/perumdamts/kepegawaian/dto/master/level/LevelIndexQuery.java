package id.perumdamts.kepegawaian.dto.master.level;

import id.perumdamts.kepegawaian.dto.master.level.commons.PagedRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LevelIndexQuery extends PagedRequest {
    private String nama;
}
