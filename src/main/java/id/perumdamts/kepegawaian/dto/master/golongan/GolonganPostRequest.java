package id.perumdamts.kepegawaian.dto.master.golongan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@Data
public class GolonganPostRequest {
    private String golongan;
    private String pangkat;

    @JsonIgnore
    public Specification<Golongan> getSpecification() {
        return SpecificationBuilder.<Golongan>of()
                .addEqual(golongan,"golongan")
                .addEqual(pangkat,"pangkat")
                .build();
    }

    public static Golongan toEntity(GolonganPostRequest request) {
        return new Golongan(request.getGolongan(), request.getPangkat());
    }

    public static Golongan toEntity(GolonganPostRequest request, Long id) {
        return new Golongan(id, request.getGolongan(), request.getPangkat());
    }

    public static List<Golongan> toEntities(List<GolonganPostRequest> requests) {
        return requests.stream().map(GolonganPostRequest::toEntity).toList();
    }
}
