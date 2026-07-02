package id.perumdamts.kepegawaian.dto.cuti.pengajuan;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EJenisPengajuanCuti;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CutiPengajuanRequest extends PagedRequest {
    private Long id;
    private Long pegawaiId;
    private String nipam;
    private String nama;
    private Integer tahun;
    private Long jabatanId;
    private Long picSaatIniId;
    private EApprovalCutiStatus approvalCutiStatus;
    private EJenisPengajuanCuti jenisPengajuanCuti;
}
