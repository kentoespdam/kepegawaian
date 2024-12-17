package id.perumdamts.kepegawaian.dto.profil.lampiranProfil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.profil.LampiranProfil;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

@Data
public class LampiranProfilAcceptRequest {
    @NotNull(message = "ID is required")
    @Min(value = 1, message = "ID is required")
    private Long id;
    @NotNull(message = "Jenis Lampiran is Required")
    private EJenisLampiranProfil ref;
    @NotNull(message = "Status is required")
    @Min(value = 1, message = "Referensi ID is required")
    private Long refId;

    @JsonIgnore
    public Specification<LampiranProfil> getSpecification() {
        return (root, query, cb) -> cb.and(
                cb.equal(root.get("id"), id),
                cb.equal(root.get("ref"), ref),
                cb.equal(root.get("refId"), refId)
        );
    }

    public static LampiranProfil toEntity(LampiranProfil entity, String oleh) {
        entity.setDisetujui(true);
        entity.setDisetujuiOleh(oleh);
        entity.setTanggalDisetujui(LocalDateTime.now());
        return entity;
    }
}
