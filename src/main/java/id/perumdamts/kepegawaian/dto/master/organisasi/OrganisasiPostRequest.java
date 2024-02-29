package id.perumdamts.kepegawaian.dto.master.organisasi;

import id.perumdamts.kepegawaian.entities.master.Organisasi;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class OrganisasiPostRequest {
    private Long parentId;
    private Integer levelOrganisasi;
    @NotEmpty(message = "Nama tidak boleh kosong")
    private String nama;

    public static Organisasi toEntity(OrganisasiPostRequest request) {
        Organisasi organisasi = new Organisasi();
        organisasi.setParentId(request.getParentId());
        organisasi.setLevelOrg(request.getLevelOrganisasi());
        organisasi.setNama(request.getNama());
        return organisasi;
    }
}
