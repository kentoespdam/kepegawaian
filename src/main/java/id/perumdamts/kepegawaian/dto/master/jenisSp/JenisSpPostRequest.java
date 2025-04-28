package id.perumdamts.kepegawaian.dto.master.jenisSp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.JenisSp;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@Data
public class JenisSpPostRequest {
    private String kode;
    private String nama;

    @JsonIgnore
    public Specification<JenisSp> getSpecification() {
        Specification<JenisSp> kodeSpec = Objects.isNull(kode) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("kode"), kode);
        return Specification.where(kodeSpec);
    }

    public static JenisSp toEntity(JenisSpPostRequest request) {
        JenisSp entity = new JenisSp();
        entity.setKode(request.getKode());
        entity.setNama(request.getNama());
        return entity;
    }
}
