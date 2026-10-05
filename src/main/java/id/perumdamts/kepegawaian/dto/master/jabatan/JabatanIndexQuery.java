package id.perumdamts.kepegawaian.dto.master.jabatan;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class JabatanIndexQuery extends PagedRequest {
    private String kode;
    private String nama;
    private Long parentId;
    private Long organisasiId;
    private Long levelId;


}
