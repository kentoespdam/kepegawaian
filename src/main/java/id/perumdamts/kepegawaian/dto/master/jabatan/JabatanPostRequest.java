package id.perumdamts.kepegawaian.dto.master.jabatan;

import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;

@Data
public class JabatanPostRequest {
    @Min(value = 1, message = "Jabatan Induk ID must be greater than or equal to 1")
    private Long parentId;
    @Min(value = 1, message = "Organisasi ID must be greater than or equal to 1")
    private Long organisasiId;
    @Min(value = 1, message = "Level ID must be greater than or equal to 1")
    private Long levelId;
    @NotEmpty(message = "Nama is required")
    private String nama;

    public Specification<Jabatan> getSpecification() {
        Specification<Jabatan> jabatanSpec = Objects.isNull(parentId) ? null :
                (root, query, cb) -> cb.equal(root.get("jabatan").get("id"), parentId);
        Specification<Jabatan> organisasiSpec = Objects.isNull(organisasiId) ? null :
                (root, query, cb) -> cb.equal(root.get("organisasi").get("id"), organisasiId);
        Specification<Jabatan> levelSpec = Objects.isNull(levelId) ? null :
                (root, query, cb) -> cb.equal(root.get("level").get("id"), organisasiId);
        Specification<Jabatan> namaSpec = Objects.isNull(nama) ? null :
                (root, query, cb) -> cb.equal(root.get("nama"), nama);
        return Specification.where(jabatanSpec).and(organisasiSpec)
                .and(levelSpec).and(namaSpec);
    }

    public static Jabatan toEntity(JabatanPostRequest request) {
        Jabatan entity = new Jabatan();
        if (Objects.nonNull(request.getParentId()))
            entity.setJabatan(new Jabatan(request.getParentId()));
        entity.setOrganisasi(new Organisasi(request.getOrganisasiId()));
        entity.setLevel(new Level(request.getLevelId()));
        entity.setNama(request.getNama());
        return entity;
    }

    public static Jabatan toEntity(JabatanPostRequest request, Jabatan parent) {
        Jabatan entity = new Jabatan();
        if (Objects.nonNull(parent))
            entity.setJabatan(parent);
        entity.setOrganisasi(new Organisasi(request.getOrganisasiId()));
        entity.setLevel(new Level(request.getLevelId()));
        entity.setNama(request.getNama());
        return entity;
    }

    public static Jabatan toEntity(JabatanPostRequest request, Long id) {
        Jabatan jabatan = JabatanPostRequest.toEntity(request);
        jabatan.setId(id);
        return jabatan;
    }

    public static List<Jabatan> toEntities(List<JabatanPostRequest> requests) {
        return requests.stream().map(JabatanPostRequest::toEntity).toList();
    }
}
