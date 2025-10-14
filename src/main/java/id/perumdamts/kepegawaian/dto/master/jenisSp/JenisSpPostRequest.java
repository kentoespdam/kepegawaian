package id.perumdamts.kepegawaian.dto.master.jenisSp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.JenisSp;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class JenisSpPostRequest {
    private String kode;
    private String nama;

    @JsonIgnore
    public Specification<JenisSp> getSpecification() {
        return SpecificationBuilder.<JenisSp>of()
                .addEqual(kode, "kode")
                .build();
    }

    public static JenisSp toEntity(JenisSpPostRequest request) {
        JenisSp entity = new JenisSp();
        entity.setKode(request.getKode());
        entity.setNama(request.getNama());
        return entity;
    }
}
