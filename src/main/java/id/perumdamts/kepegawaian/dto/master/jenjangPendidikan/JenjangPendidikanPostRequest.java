package id.perumdamts.kepegawaian.dto.master.jenjangPendidikan;

import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class JenjangPendidikanPostRequest {
    @NotEmpty(message = "Nama is required")
    private String nama;
    private String shortName;
    @Min(value = 1, message = "Seq is required")
    private Integer seq;
    private Boolean isStatistik = Boolean.FALSE;



}
