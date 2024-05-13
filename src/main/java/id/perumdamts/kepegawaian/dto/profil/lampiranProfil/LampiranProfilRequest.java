package id.perumdamts.kepegawaian.dto.profil.lampiranProfil;

import id.perumdamts.kepegawaian.entities.profil.LampiranProfil;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class LampiranProfilRequest {
    @Min(value = 1, message = "Jenis lampiran profil ID is required")
    private Long ref;
    @Min(value = 1, message = "Lampiran profil ID is required")
    private Long ref_id;

    public Specification<LampiranProfil> getSpecification() {
        Specification<LampiranProfil> refSpec = (root, query, cb) -> cb.equal(root.get("ref").get("id"), ref);
        Specification<LampiranProfil> refIdSpec = (root, query, cb) -> cb.equal(root.get("ref_id"), ref_id);
        return Specification.where(refSpec).and(refIdSpec);
    }
}
