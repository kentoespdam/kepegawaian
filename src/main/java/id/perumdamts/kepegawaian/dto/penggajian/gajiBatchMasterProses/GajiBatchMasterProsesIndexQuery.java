package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiBatchMasterProsesIndexQuery extends PagedRequest {
    private Long batchMasterId;
    private EJenisGaji jenisGaji;
    private String kode;
}
