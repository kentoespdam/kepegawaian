package id.perumdamts.kepegawaian.dto.master.jenisPelatihan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.JenisPelatihan;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@Data
public class JenisPelatihanPostRequest {
    @NotEmpty(message = "Nama is required")
    private String nama;

    @JsonIgnore
    public Specification<JenisPelatihan> getSpecification() {
        return SpecificationBuilder.<JenisPelatihan>of()
                .addEqual(nama,"nama")
                .build();
    }

    public static JenisPelatihan toEntity(JenisPelatihanPostRequest request) {
        return new JenisPelatihan(request.getNama());
    }

    public static JenisPelatihan toEntity(JenisPelatihanPostRequest request, JenisPelatihan entity) {
        entity.setNama(request.getNama());
        return entity;
    }

    public static List<JenisPelatihan> toEntities(List<JenisPelatihanPostRequest> requests) {
        return requests.stream().map(JenisPelatihanPostRequest::toEntity).toList();
    }
}
