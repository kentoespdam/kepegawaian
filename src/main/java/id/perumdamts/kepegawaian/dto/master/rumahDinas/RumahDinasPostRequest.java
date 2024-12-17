package id.perumdamts.kepegawaian.dto.master.rumahDinas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.RumahDinas;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class RumahDinasPostRequest {
    private String nama;
    private Double nilai;

    @JsonIgnore
    public Specification<RumahDinas> getSpecification() {
        return Specification.where(
                (root, query, cb) ->
                        cb.equal(root.get("nama"), nama)
        );
    }

    public static RumahDinas toEntity(RumahDinasPostRequest request) {
        RumahDinas entity = new RumahDinas();
        entity.setNama(request.getNama());
        entity.setNilai(request.getNilai());
        return entity;
    }
}
