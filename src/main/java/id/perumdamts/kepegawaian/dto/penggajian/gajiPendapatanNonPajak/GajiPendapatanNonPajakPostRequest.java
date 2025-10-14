package id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPendapatanNonPajak;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
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
        return SpecificationBuilder.<GajiPendapatanNonPajak>of()
                .addEqual(kode, "kode")
                .build();
    }

    public static GajiPendapatanNonPajak toEntity(GajiPendapatanNonPajakPostRequest request) {
        GajiPendapatanNonPajak entity = new GajiPendapatanNonPajak();
        entity.setKode(request.getKode());
        entity.setNominal(request.getNominal());
        entity.setNotes(request.getNotes());
        return entity;
    }
}
