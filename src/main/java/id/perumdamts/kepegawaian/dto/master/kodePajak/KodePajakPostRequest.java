package id.perumdamts.kepegawaian.dto.master.kodePajak;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.KodePajak;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;

@Data
public class KodePajakPostRequest {
    @NotEmpty(message = "Kode is required")
    private String kode;
    @NotNull(message = "Nominal is required")
    @Min(value = 1, message = "Nominal must be greater than or equal to 1")
    private Double nominal;

    @JsonIgnore
    public Specification<KodePajak> getSpecification() {
        Specification<KodePajak> kodeSpec = Objects.isNull(kode) ? null :
                (root, query, cb) -> cb.equal(root.get("kode"), kode);
        Specification<KodePajak> nominalSpec = Objects.isNull(nominal) ? null :
                (root, query, cb) -> cb.equal(root.get("nominal"), nominal);
        return Specification.where(kodeSpec).and(nominalSpec);
    }

    public static KodePajak toEntity(KodePajakPostRequest request) {
        KodePajak entity = new KodePajak();
        entity.setKode(request.getKode());
        entity.setNominal(request.getNominal());
        return entity;
    }

    public static List<KodePajak> toEntities(List<KodePajakPostRequest> requests) {
        return requests.stream().map(KodePajakPostRequest::toEntity).toList();
    }
}
