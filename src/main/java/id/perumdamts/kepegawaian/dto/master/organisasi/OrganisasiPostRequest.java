package id.perumdamts.kepegawaian.dto.master.organisasi;

import id.perumdamts.kepegawaian.entities.master.Organisasi;
import lombok.Data;

@Data
public class OrganisasiPostRequest {
    private String nama;

    public Organisasi toEntity(OrganisasiPostRequest request) {
        Organisasi organisasi = new Organisasi();
        organisasi.setNama(request.getNama());
        return organisasi;
    }
}
