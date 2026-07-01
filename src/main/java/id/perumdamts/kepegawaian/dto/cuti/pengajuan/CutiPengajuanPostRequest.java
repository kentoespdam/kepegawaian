package id.perumdamts.kepegawaian.dto.cuti.pengajuan;

import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EJenisPengajuanCuti;
import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;

@Data
public class CutiPengajuanPostRequest {
    @NotNull(message = "CSRF token is required")
    @NotBlank(message = "CSRF token is required")
    private String csrfToken;
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




    public static CutiPegawai toEntity(CutiPengajuanPostRequest request, Pegawai pegawai, CutiJenis cutiJenis, CutiJenis subJenisCuti) {
        CutiPegawai entity = new CutiPegawai();
        entity.setPegawai(pegawai);
        entity.setNama(pegawai.getBiodata().getNama());
        entity.setNipam(pegawai.getNipam());
        entity.setPangkatGolongan(pegawai.getGolongan().getGolongan() + " - " + pegawai.getGolongan().getPangkat());
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
        return entity;
    }
}
