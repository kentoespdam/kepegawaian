package id.perumdamts.kepegawaian.dto.profil.lampiranProfil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.profil.LampiranProfil;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public class LampiranProfilPostRequest implements Serializable {
    private EJenisLampiranProfil ref;
    @Min(value = 1, message = "Ref ID must be greater than or equal to 1")
    private Long refId;
    @NotNull(message = "File Name is required")
    private MultipartFile fileName;
    private String notes;

    @JsonIgnore
    public Specification<LampiranProfil> getSpecification() {
        return SpecificationBuilder.<LampiranProfil>of()
                .addEqual(ref, "ref")
                .addEqual(refId, "refId")
                .addEqual(fileName, "fileName")
                .build();
    }
}
