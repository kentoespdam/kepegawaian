package id.perumdamts.kepegawaian.dto.cuti.pengajuan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EJenisPengajuanCuti;
import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.Expression;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Data
public class CutiPengajuanPostRequest {
    @NotNull(message = "Pegawai is required")
    @Min(value = 1, message = "Pegawai is required")
    private Long pegawaiId;
    @NotNull(message = "Jabatan is required")
    @Min(value = 1, message = "Jabatan is required")
    private Long jenisCutiId;
    private Long subJenisCutiId;
    @NotNull(message = "Tanggal mulai cuti is required")
    private LocalDate tanggalMulai;
    @NotNull(message = "Tanggal mulai cuti is required")
    private LocalDate tanggalSelesai;
    @NotNull(message = "Jumlah hari kerja cuti is required")
    private Integer jumlahHariKerja;
    @NotNull(message = "Alasan cuti is required")
    @NotBlank(message = "Alasan cuti is required")
    private String alasan;


    @JsonIgnore
    public Specification<CutiPegawai> getPendingStatusSpecification() {
        return (root, query, criteriaBuilder) -> {
            Expression<LocalDate> tanggalExpression = root.get("tanggalMulai");
            Expression<Integer> yearExpression = criteriaBuilder.function("YEAR", Integer.class, tanggalExpression);
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("pegawai").get("id"), pegawaiId),
                    criteriaBuilder.equal(yearExpression, tanggalMulai.getYear()),
                    criteriaBuilder.equal(root.get("approvalCutiStatus"), EApprovalCutiStatus.PENDING)
            );
        };
    }

    @JsonIgnore
    public Specification<CutiPegawai> getSpecificationByJenisCuti(Long jenisCutiId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get("pegawai").get("id"), pegawaiId),
                criteriaBuilder.equal(root.get("jenisCuti").get("id"), jenisCutiId),
                criteriaBuilder.in(root.get("approvalCutiStatus")).value(List.of(
                        EApprovalCutiStatus.PENDING,
                        EApprovalCutiStatus.APPROVED,
                        EApprovalCutiStatus.CONFIRMED,
                        EApprovalCutiStatus.RETURNED
                ))
        );
    }

    @JsonIgnore
    public Specification<CutiPegawai> getSpecificationByJenisCuti(Long jenisCutiId, LocalDate tanggalMulai) {
        return (root, query, criteriaBuilder) -> {
            Expression<LocalDate> tanggalExpression = root.get("tanggalMulai");
            Expression<Integer> yearExpression = criteriaBuilder.function("YEAR", Integer.class, tanggalExpression);
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("pegawai").get("id"), pegawaiId),
                    criteriaBuilder.equal(yearExpression, tanggalMulai.getYear()),
                    criteriaBuilder.equal(root.get("jenisCuti").get("id"), jenisCutiId),
                    criteriaBuilder.in(root.get("approvalCutiStatus")).value(List.of(
                            EApprovalCutiStatus.PENDING,
                            EApprovalCutiStatus.APPROVED,
                            EApprovalCutiStatus.CONFIRMED,
                            EApprovalCutiStatus.RETURNED
                    ))
            );
        };
    }

    @JsonIgnore
    public Specification<CutiPegawai> getPendingKlaimSpecification() {
        return (root, query, criteriaBuilder) -> {
            Expression<LocalDate> tanggalExpression = root.get("tanggalMulai");
            Expression<Integer> yearExpression = criteriaBuilder.function("YEAR", Integer.class, tanggalExpression);
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("pegawai").get("id"), pegawaiId),
                    criteriaBuilder.equal(yearExpression, tanggalMulai.getYear()),
                    criteriaBuilder.equal(root.get("approvalCutiStatus"), EApprovalCutiStatus.PENDING)
            );
        };
    }

    public static CutiPegawai toEntity(CutiPengajuanPostRequest request, Pegawai pegawai, CutiJenis cutiJenis, CutiJenis subJenisCuti, Jabatan atasanLangsung) {
        CutiPegawai entity = new CutiPegawai();
        entity.setPegawai(pegawai);
        entity.setNama(pegawai.getBiodata().getNama());
        entity.setNipam(pegawai.getNipam());
        entity.setOrganisasi(pegawai.getOrganisasi());
        entity.setJabatan(pegawai.getJabatan());
        entity.setJenisPengajuanCuti(EJenisPengajuanCuti.PENGAJUAN_CUTI);
        entity.setJenisCuti(cutiJenis);
        if (Objects.nonNull(subJenisCuti))
            entity.setSubJenisCuti(subJenisCuti);
        entity.setTanggalMulai(request.getTanggalMulai());
        entity.setTanggalSelesai(request.getTanggalSelesai());
        entity.setAlasan(request.getAlasan());
        entity.setApprovalCutiStatus(EApprovalCutiStatus.PENDING);
        entity.setApprovalLevel(1);
        entity.setPicSaatIni(atasanLangsung);
        return entity;
    }

    public static CutiPegawai toEntity(CutiPengajuanPostRequest request, int totalKuota, Pegawai pegawai, CutiJenis jenisCuti, Jabatan picSaatIni, @Nullable CutiJenis subJenisCuti) {
        CutiPegawai result = new CutiPegawai();
        result.setPegawai(pegawai);
        result.setNama(pegawai.getBiodata().getNama());
        result.setNipam(pegawai.getNipam());
        result.setOrganisasi(pegawai.getOrganisasi());
        result.setJabatan(pegawai.getJabatan());
        result.setJenisPengajuanCuti(EJenisPengajuanCuti.PENGAJUAN_CUTI);
        result.setJenisCuti(jenisCuti);
        result.setSubJenisCuti(subJenisCuti);
        result.setTanggalMulai(request.getTanggalMulai());
        result.setTanggalSelesai(request.getTanggalSelesai());
        result.setJumlahHariKerja(request.getJumlahHariKerja());
        result.setKuotaAwal(totalKuota);
        result.setKuotaAkhir(totalKuota - request.getJumlahHariKerja());
        result.setAlasan(request.getAlasan());
        result.setApprovalCutiStatus(EApprovalCutiStatus.PENDING);
        result.setApprovalLevel(1);
        result.setPicSaatIni(picSaatIni);
        return result;
    }

}
