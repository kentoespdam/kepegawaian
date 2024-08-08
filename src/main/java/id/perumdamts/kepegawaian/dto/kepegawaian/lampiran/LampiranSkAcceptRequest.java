package id.perumdamts.kepegawaian.dto.kepegawaian.lampiran;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.penggajian.LampiranSk;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

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

    @JsonIgnore
    public Specification<LampiranSk> getSpecification() {
        return (root, query, cb) ->
                cb.and(
                        cb.equal(root.get("id"), id),
                        cb.equal(root.get("ref"), ref),
                        cb.equal(root.get("refId"), refId)
                );
    }

    public static LampiranSk toEntity(LampiranSk entity, String oleh){
        entity.setDisetujui(true);
        entity.setDisetujuiOleh(oleh);
        entity.setTanggalDisetujui(LocalDateTime.now());
        return entity;
    }
}
