package id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiParameterSettingIndexQuery extends PagedRequest {
    private String kode;
}
