package id.perumdamts.kepegawaian.dto.master.organisasi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@Data
public class OrganisasiPostRequest {
    private String kode;
    private Long parentId;
    private Integer levelOrganisasi;
    @NotEmpty(message = "Nama tidak boleh kosong")
    private String nama;
    private String shortName;

    @JsonIgnore
    public Specification<Organisasi> getSpecification() {
        Specification<Organisasi> kodeSpec = Objects.isNull(kode) ? null :
                (root, query, cb) -> cb.equal(root.get("kode"), kode);
        Specification<Organisasi> parentIdSpec = Objects.isNull(parentId) ? null :
                (root, query, cb) -> cb.equal(root.get("parent").get("id"), parentId);
        Specification<Organisasi> levelSpec = Objects.isNull(levelOrganisasi) ? null :
                (root, query, cb) -> cb.equal(root.get("levelOrg"), levelOrganisasi);
        Specification<Organisasi> namaSpec = Objects.isNull(nama) ? null :
                (root, query, cb) -> cb.equal(root.get("nama"), nama);
        return Specification.where(kodeSpec).and(parentIdSpec).and(levelSpec).and(namaSpec);
    }

    public static Organisasi toEntity(OrganisasiPostRequest request, Organisasi parent) {
        Organisasi organisasi = new Organisasi();
        organisasi.setKode(request.getKode());
        if (parent != null)
            organisasi.setParent(parent);
        organisasi.setLevelOrg(request.getLevelOrganisasi());
        organisasi.setNama(request.getNama());
        organisasi.setShortName(request.getShortName());
        return organisasi;
    }
}
