package id.perumdamts.kepegawaian.dto.master.golongan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;

@Data
public class GolonganPostRequest {
    private String golongan;
    private String pangkat;

    @JsonIgnore
    public Specification<Golongan> getSpecification() {
        Specification<Golongan> golonganSpec = Objects.isNull(golongan) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("golongan"), golongan);
        Specification<Golongan> pangkatSpec = Objects.isNull(pangkat) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("pangkat"), pangkat);

        return Specification.where(golonganSpec).and(pangkatSpec);
    }

    public static Golongan toEntity(GolonganPostRequest request) {
        Golongan entity= new Golongan();
        entity.setGolongan(request.getGolongan());
        entity.setPangkat(request.getPangkat());
        return entity;
    }

    public static Golongan toEntity(GolonganPostRequest request, Long id) {
        Golongan entity = toEntity(request);
        entity.setId(id);
        return entity;
    }

    public static List<Golongan> toEntities(List<GolonganPostRequest> requests) {
        return requests.stream().map(GolonganPostRequest::toEntity).toList();
    }
}
