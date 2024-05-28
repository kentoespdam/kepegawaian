package id.perumdamts.kepegawaian.dto.master.apd;

import id.perumdamts.kepegawaian.entities.master.Apd;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@Data
public class ApdPostRequest {
    @NotNull(message = "Profesi ID is required")
    @Min(value = 1, message = "Profesi ID must be greater than or equal to 1")
    private Long profesiId;
    @NotEmpty(message = "Nama is required")
    private String nama;

    public Specification<Apd> getSpecification() {
        Specification<Apd> namaSpec = Objects.isNull(nama) ? null : (root, query, cb) -> cb.equal(root.get("nama"), nama);
        Specification<Apd> profesiSpec = Objects.isNull(profesiId) ? null : (root, query, cb) -> cb.equal(root.get("profesi").get("id"), profesiId);
        return Specification.where(namaSpec).and(profesiSpec);
    }

    public static Apd toEntity(ApdPostRequest request, Profesi profesi) {
        Apd entity = new Apd();
        entity.setProfesi(profesi);
        entity.setNama(request.getNama());
        return entity;
    }
}
