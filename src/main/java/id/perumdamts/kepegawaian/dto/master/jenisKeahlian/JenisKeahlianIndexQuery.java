package id.perumdamts.kepegawaian.dto.master.jenisKeahlian;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class JenisKeahlianIndexQuery extends CommonPageRequest {
    private String nama;
}
