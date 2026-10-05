package id.perumdamts.kepegawaian.dto.kepegawaian.lampiran;

import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.LampiranSk;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LampiranSkAcceptRequest {
    @NotNull(message = "ID is required")
    @Min(value = 1, message = "ID is required")
    private Long id;
    @NotNull(message = "Jenis Lampiran is Required")
    private EJenisSk ref;
    @NotNull(message = "Status is required")
    @Min(value = 1, message = "Referensi ID is required")
    private Long refId;




}
