package id.perumdamts.kepegawaian.dto.master.organisasi;

import id.perumdamts.kepegawaian.entities.master.Organisasi;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrganisasiPostRequest {
    private Long parentId;
    private Integer levelOrganisasi;
    @NotEmpty(message = "Nama tidak boleh kosong")
    private String nama;

    public static Organisasi toEntity(OrganisasiPostRequest request) {
        Organisasi organisasi = new Organisasi();
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
