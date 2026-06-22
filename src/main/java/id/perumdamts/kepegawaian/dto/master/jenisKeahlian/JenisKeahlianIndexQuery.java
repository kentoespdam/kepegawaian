package id.perumdamts.kepegawaian.dto.master.jenisKeahlian;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class JenisKeahlianIndexQuery extends PagedRequest {
    private String nama;
}
