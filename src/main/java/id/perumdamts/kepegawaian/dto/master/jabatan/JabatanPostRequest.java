package id.perumdamts.kepegawaian.dto.master.jabatan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@Data
public class JabatanPostRequest {
    @NotEmpty(message = "Kode is required")
    @NotNull(message = "Kode is required")
    private String kode;
    @Min(value = 1, message = "Jabatan Induk ID must be greater than or equal to 1")
    private Long parentId;
    @Min(value = 1, message = "Organisasi ID must be greater than or equal to 1")
    private Long organisasiId;
    @Min(value = 1, message = "Level ID must be greater than or equal to 1")
    private Long levelId;
    @NotEmpty(message = "Nama is required")
    private String nama;

    @JsonIgnore
    public Specification<Jabatan> getSpecification() {
        Specification<Jabatan> kodeSpec = Objects.isNull(kode) ? null :
                (root, query, cb) -> cb.equal(root.get("kode"), kode);
        Specification<Jabatan> jabatanSpec = Objects.isNull(parentId) ? null :
                (root, query, cb) -> cb.equal(root.get("parent").get("id"), parentId);
        Specification<Jabatan> organisasiSpec = Objects.isNull(organisasiId) ? null :
                (root, query, cb) -> cb.equal(root.get("organisasi").get("id"), organisasiId);
        Specification<Jabatan> levelSpec = Objects.isNull(levelId) ? null :
                (root, query, cb) -> cb.equal(root.get("level").get("id"), organisasiId);
        Specification<Jabatan> namaSpec = Objects.isNull(nama) ? null :
                (root, query, cb) -> cb.equal(root.get("nama"), nama);
        return Specification.where(kodeSpec).and(jabatanSpec).and(organisasiSpec)
                .and(levelSpec).and(namaSpec);
    }

    public static Jabatan toEntity(
            JabatanPostRequest request,
            Jabatan parent,
            Organisasi organisasi,
            Level level
    ) {
        Jabatan entity = new Jabatan();
        entity.setKode(request.getKode());
        if (Objects.nonNull(parent))
            entity.setParent(parent);
        entity.setOrganisasi(organisasi);
        entity.setLevel(level);
        entity.setNama(request.getNama());
        return entity;
    }
}
