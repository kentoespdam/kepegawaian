package id.perumdamts.kepegawaian.dto.master.organisasi;

import id.perumdamts.kepegawaian.entities.master.Organisasi;
import lombok.Data;

@Data
public class OrganisasiResponse {
    private Long id;
    private String kode;
    private OrganisasiMiniResponse parent;
    private Integer levelOrganisasi;
    private String nama;

    public static OrganisasiResponse from(Organisasi organisasi) {
        OrganisasiResponse response = new OrganisasiResponse();
        response.setId(organisasi.getId());
        response.setKode(organisasi.getKode());
        if (organisasi.getParent() != null)
            response.setParent(OrganisasiMiniResponse.from(organisasi.getParent()));
        response.setLevelOrganisasi(organisasi.getLevelOrg());
        response.setNama(organisasi.getNama());
        return response;
    }
}
