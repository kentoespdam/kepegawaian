package id.perumdamts.kepegawaian.dto.kepegawaian.lampiran;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.LampiranSk;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

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

    @JsonIgnore
    public Specification<LampiranSk> getSpecification() {
        return SpecificationBuilder.<LampiranSk>of()
                .addEqual(ref, "ref")
                .addEqual(refId, "refId")
                .addEqual(fileName.getName(), "fileName")
                .build();
    }


}
