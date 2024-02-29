package id.perumdamts.kepegawaian.dto.master.organisasi;

import id.perumdamts.kepegawaian.entities.master.Organisasi;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrganisasiPutRequest extends OrganisasiPostRequest {
    public static Organisasi toEntity(OrganisasiPutRequest request, Long id) {
        Organisasi organisasi = new Organisasi(id);
        if (request.getParentId() != null && request.getParentId() != 0L)
            organisasi.setOrganisasi(new Organisasi(request.getParentId()));
        organisasi.setNama(request.getNama());
        return organisasi;
    }

}
