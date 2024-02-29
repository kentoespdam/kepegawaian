package id.perumdamts.kepegawaian.dto.master.organisasi;

import id.perumdamts.kepegawaian.entities.master.Organisasi;
import lombok.Data;

@Data
public class OrganisasiResponse {
    private Long id;
    private Long parentId;
    private Integer levelOrganisasi;
    private String nama;

    public static OrganisasiResponse from(Organisasi organisasi) {
        OrganisasiResponse response = new OrganisasiResponse();
        response.setId(organisasi.getId());
        response.setParentId(organisasi.getParentId());
        response.setLevelOrganisasi(organisasi.getLevelOrg());
        response.setNama(organisasi.getNama());
        return response;
    }
}
