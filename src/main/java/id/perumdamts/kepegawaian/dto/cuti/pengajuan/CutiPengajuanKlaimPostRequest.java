package id.perumdamts.kepegawaian.dto.cuti.pengajuan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EJenisPengajuanCuti;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

@Data
public class CutiPengajuanKlaimPostRequest {
//    @NotNull(message = "CSRF token is required")
//    @NotBlank(message = "CSRF token is required")
//    private String csrfToken;
    @NotNull(message = "Referensi Cuti is required")
    @Min(value = 1, message = "Referensi Cuti is required")
    private Long refCutiId;
    @NotNull(message = "Pegawai is required")
    @Min(value = 1, message = "Pegawai is required")
    private Long pegawaiId;
    private String keterangan;
    @NotNull(message = "Tanggal Klaim cuti is required")
    @NotEmpty(message = "Tanggal Klaim cuti is required")
    private List<LocalDate> listHari;

    public List<LocalDate> getListHari() {
        return listHari.stream().sorted().toList();
    }

    @JsonIgnore
    public Specification<CutiPegawai> getSpecification() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get("refCuti").get("id"), refCutiId),
                criteriaBuilder.in(root.get("approvalCutiStatus")).value(
                        List.of(
                                EApprovalCutiStatus.PENDING,
                                EApprovalCutiStatus.APPROVED,
                                EApprovalCutiStatus.CONFIRMED,
                                EApprovalCutiStatus.RETURNED
                        )
                )
        );
    }

    public static CutiPegawai toEntity(CutiPegawai cutiPegawai, CutiPengajuanKlaimPostRequest request) {
        CutiPegawai entity = new CutiPegawai();
        entity.setRefCuti(cutiPegawai);
        entity.setPegawai(cutiPegawai.getPegawai());
        entity.setNipam(cutiPegawai.getNipam());
        entity.setNama(cutiPegawai.getNama());
        entity.setOrganisasi(cutiPegawai.getOrganisasi());
        entity.setJabatan(cutiPegawai.getJabatan());
        entity.setJenisPengajuanCuti(EJenisPengajuanCuti.KLAIM_CUTI);
        entity.setJenisCuti(cutiPegawai.getJenisCuti());
        entity.setSubJenisCuti(cutiPegawai.getSubJenisCuti());
        entity.setAlasan(request.getKeterangan());
        entity.setApprovalCutiStatus(EApprovalCutiStatus.PENDING);
        entity.setApprovalLevel(1);
        return entity;
    }
}
