package id.perumdamts.kepegawaian.dto.master.jenisKitas;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class JenisKitasIndexQuery extends PagedRequest {
    private String nama;
}
