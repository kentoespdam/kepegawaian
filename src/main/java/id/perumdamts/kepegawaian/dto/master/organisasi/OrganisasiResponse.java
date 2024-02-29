package id.perumdamts.kepegawaian.dto.master.organisasi;

import id.perumdamts.kepegawaian.entities.master.Organisasi;
import lombok.Data;

@Data
public class OrganisasiResponse {
    private Long id;
    private OrganisasiResponse organisasi;
    private Integer levelOrganisasi;
    private String nama;

    public static OrganisasiResponse from(Organisasi organisasi) {
        OrganisasiResponse response = new OrganisasiResponse();
        response.setId(organisasi.getId());
        if (organisasi.getOrganisasi() != null) {
            OrganisasiResponse parent = getOrgParent(organisasi.getOrganisasi());
            response.setOrganisasi(parent);
        }
        response.setLevelOrganisasi(organisasi.getLevelOrg());
        response.setNama(organisasi.getNama());
        return response;
    }

    private static OrganisasiResponse getOrgParent(Organisasi organisasi) {
        OrganisasiResponse response = new OrganisasiResponse();
        response.setId(organisasi.getId());
        response.setLevelOrganisasi(organisasi.getLevelOrg());
        response.setNama(organisasi.getNama());
        return response;
    }
}
