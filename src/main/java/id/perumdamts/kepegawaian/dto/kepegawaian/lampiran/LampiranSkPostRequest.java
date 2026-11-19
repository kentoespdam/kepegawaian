package id.perumdamts.kepegawaian.dto.kepegawaian.lampiran;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LampiranSkPostRequest implements Serializable {
    @Enumerated(EnumType.ORDINAL)
    private EJenisSk ref;
    @Min(value = 1, message = "Ref ID must be greater than or equal to 1")
    private Long refId;
    @NotNull(message = "File Name is required")
    private MultipartFile fileName;
    private String notes;

}
