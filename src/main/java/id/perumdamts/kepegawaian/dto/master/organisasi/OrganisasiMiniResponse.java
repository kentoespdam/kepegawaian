package id.perumdamts.kepegawaian.dto.master.organisasi;

import id.perumdamts.kepegawaian.entities.master.Organisasi;
import lombok.Data;

@Data
public class OrganisasiMiniResponse {
    private Long id;
    private String nama;

    public static OrganisasiMiniResponse from(Organisasi organisasi) {
        OrganisasiMiniResponse response = new OrganisasiMiniResponse();
        response.setId(organisasi.getId());
        response.setNama(organisasi.getNama());
        return response;
    }
}
