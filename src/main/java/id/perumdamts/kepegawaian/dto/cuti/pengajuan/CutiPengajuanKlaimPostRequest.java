package id.perumdamts.kepegawaian.dto.cuti.pengajuan;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CutiPengajuanKlaimPostRequest {
    @NotNull(message = "CSRF token is required")
    @NotBlank(message = "CSRF token is required")
    private String csrfToken;
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



}
