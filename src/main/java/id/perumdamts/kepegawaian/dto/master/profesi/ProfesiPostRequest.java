package id.perumdamts.kepegawaian.dto.master.profesi;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ProfesiPostRequest {
    @Min(value = 1, message = "Organisasi ID must be greater than or equal to 1")
    private Long organisasiId;
    @Min(value = 1, message = "Jabatan ID must be greater than or equal to 1")
    private Long jabatanId;
    @Min(value = 1, message = "Grade ID must be greater than or equal to 1")
    private Long gradeId;
    @NotEmpty(message = "Nama Profesi is required")
    private String nama;
    @NotEmpty(message = "Detail Profesi is required")
    private String detail;
    @NotEmpty(message = "Resiko Profesi is required")
    private String resiko;
}
