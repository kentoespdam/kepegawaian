package id.perumdamts.kepegawaian.dto.master.organisasi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;

@Data
public class OrganisasiPostRequest {
    private Long parentId;
    private Integer levelOrganisasi;
    @NotEmpty(message = "Nama tidak boleh kosong")
    private String nama;

    @JsonIgnore
    public Specification<Organisasi> getSpecification() {
        Specification<Organisasi> parentIdSpec = Objects.isNull(parentId) ? null :
                (root, query, cb) -> cb.equal(root.get("organisasi").get("id"), parentId);
        Specification<Organisasi> levelSpec = Objects.isNull(levelOrganisasi) ? null :
                (root, query, cb) -> cb.equal(root.get("levelOrganisasi"), levelOrganisasi);
        Specification<Organisasi> namaSpec = Objects.isNull(nama) ? null :
                (root, query, cb) -> cb.equal(root.get("nama"), nama);
        return Specification.where(parentIdSpec).and(levelSpec).and(namaSpec);
    }

    public static Organisasi toEntity(OrganisasiPostRequest request) {
        Organisasi organisasi = new Organisasi();
        if (request.getParentId() != null && request.getParentId() != 0L)
            organisasi.setOrganisasi(new Organisasi(request.getParentId()));
        organisasi.setLevelOrg(request.getLevelOrganisasi());
        organisasi.setNama(request.getNama());
        return organisasi;
    }

    public static Organisasi toEntity(OrganisasiPostRequest request, Long id) {
        Organisasi organisasi = new Organisasi(id);
        if (request.getParentId() != null && request.getParentId() != 0L)
            organisasi.setOrganisasi(new Organisasi(request.getParentId()));
        organisasi.setLevelOrg(request.getLevelOrganisasi());
        organisasi.setNama(request.getNama());
        return organisasi;
    }

    public static List<Organisasi> toEntities(List<OrganisasiPostRequest> orgs) {
        return orgs.stream().map(OrganisasiPostRequest::toEntity).toList();
    }
}
