package id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak;

import id.perumdamts.kepegawaian.entities.penggajian.GajiPendapatanNonPajak;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GajiPendapatanNonPajakPostRequest {
    @NotEmpty(message = "Kode is required")
    private String kode;
    @NotNull(message = "Nominal is required")
    private Double nominal;
    private String notes;



}
