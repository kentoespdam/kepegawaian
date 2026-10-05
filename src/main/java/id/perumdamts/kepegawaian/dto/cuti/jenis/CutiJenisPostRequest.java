package id.perumdamts.kepegawaian.dto.cuti.jenis;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CutiJenisPostRequest {
    private Long parentId;
    @NotNull(message = "Nama is required")
    @NotEmpty(message = "Nama is required")
    private String nama;
    private Integer maxHari = 0;
    private Boolean potongKuotaTahunan = false;
}
