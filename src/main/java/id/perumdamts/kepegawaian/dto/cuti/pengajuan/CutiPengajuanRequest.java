package id.perumdamts.kepegawaian.dto.cuti.pengajuan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EJenisPengajuanCuti;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class CutiPengajuanRequest extends CommonPageRequest {
    private Long id;
    private Long pegawaiId;
    private String nipam;
    private String nama;
    private Integer tahun;
    private Long jabatanId;
    private Long picSaatIniId;
    private EApprovalCutiStatus approvalCutiStatus;
    private EJenisPengajuanCuti jenisPengajuanCuti;

    @JsonIgnore
    public Specification<CutiPegawai> getSpecification() {
        return SpecificationBuilder.<CutiPegawai>of()
                .addEqual(id, "id")
                .addEqual(pegawaiId, "pegawai", "id")
                .addLike(nipam, "pegawai", "nipam")
                .addLike(nama, "pegawai", "biodata", "nama")
                .addEqual(tahun, "tahun")
                .addEqual(jabatanId, "jabatan", "id")
                .addEqual(picSaatIniId, "picSaatIni", "id")
                .addEqual(approvalCutiStatus, "approvalCutiStatus")
                .addEqual(jenisPengajuanCuti, "jenisPengajuanCuti")
                .build();
    }
}
