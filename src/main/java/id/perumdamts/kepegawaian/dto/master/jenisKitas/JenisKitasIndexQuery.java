package id.perumdamts.kepegawaian.dto.master.jenisKitas;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class JenisKitasIndexQuery extends CommonPageRequest {
    private String nama;
}
