package id.perumdamts.kepegawaian.dto.master.level;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LevelIndexQuery extends CommonPageRequest {
    private String nama;
}
