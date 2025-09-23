package id.perumdamts.kepegawaian.dto.master.organisasi;

import id.perumdamts.kepegawaian.entities.master.Organisasi;
import lombok.Data;

import java.util.Objects;

@Data
public class OrganisasiResponse {
    private Long id;
    private String kode;
    private OrganisasiMiniResponse parent;
    private Integer levelOrganisasi;
    private String nama;
    private String shortName;
    private String category;

    public static OrganisasiResponse from(Organisasi organisasi) {
        if (Objects.isNull(organisasi)) return null;
        OrganisasiResponse response = new OrganisasiResponse();
        response.setId(organisasi.getId());
        response.setKode(organisasi.getKode());
        if (organisasi.getParent() != null)
            response.setParent(OrganisasiMiniResponse.from(organisasi.getParent()));
        response.setLevelOrganisasi(organisasi.getLevelOrg());
        response.setNama(organisasi.getNama());
        response.setShortName(organisasi.getShortName());
        response.setCategory(organisasi.getCategory());
        return response;
    }
}
