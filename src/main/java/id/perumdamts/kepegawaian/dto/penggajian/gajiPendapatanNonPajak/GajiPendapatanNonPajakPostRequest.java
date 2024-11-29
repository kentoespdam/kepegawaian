package id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPendapatanNonPajak;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class GajiPendapatanNonPajakPostRequest {
    @NotEmpty(message = "Kode is required")
    private String kode;
    @NotNull(message = "Nominal is required")
    private Double nominal;
    private String notes;

    @JsonIgnore
    public Specification<GajiPendapatanNonPajak> getSpecification() {
        Specification<GajiPendapatanNonPajak> kodeSpec = (root, query, cb) -> cb.equal(root.get("kode"), kode);
        return Specification.where(kodeSpec);
    }

    public static GajiPendapatanNonPajak toEntity(GajiPendapatanNonPajakPostRequest request) {
        GajiPendapatanNonPajak entity = new GajiPendapatanNonPajak();
        entity.setKode(request.getKode());
        entity.setNominal(request.getNominal());
        entity.setNotes(request.getNotes());
        return entity;
    }
}
