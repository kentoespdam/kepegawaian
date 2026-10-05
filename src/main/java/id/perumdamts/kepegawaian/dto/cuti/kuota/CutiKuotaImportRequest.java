package id.perumdamts.kepegawaian.dto.cuti.kuota;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CutiKuotaImportRequest {
    @NotNull(message = "Tahun is required")
    @Min(value = 2000, message = "Tahun is required")
    private Integer tahun;
    @NotNull(message = "File is required")
    private MultipartFile file;
}
