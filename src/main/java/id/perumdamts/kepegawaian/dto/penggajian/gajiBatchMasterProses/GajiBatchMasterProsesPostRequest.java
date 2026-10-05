package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses;

import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMasterProses;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GajiBatchMasterProsesPostRequest {
    @Min(value = 1, message = "Master Batch ID required")
    @NotNull(message = "Master Batch ID required")
    private Long batchMasterId;
    private String nama;
    private EJenisGaji jenisGaji;
    private Double nilai;



}
