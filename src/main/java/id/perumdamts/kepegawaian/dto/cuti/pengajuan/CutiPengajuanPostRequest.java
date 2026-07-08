package id.perumdamts.kepegawaian.dto.cuti.pengajuan;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

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





}
