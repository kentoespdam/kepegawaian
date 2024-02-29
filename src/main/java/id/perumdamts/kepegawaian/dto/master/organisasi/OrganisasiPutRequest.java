package id.perumdamts.kepegawaian.dto.master.organisasi;

import id.perumdamts.kepegawaian.entities.master.Organisasi;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrganisasiPutRequest extends OrganisasiPostRequest {
    public Organisasi toEntity(OrganisasiPutRequest request){
        Organisasi organisasi = new Organisasi();
        organisasi.setNama(request.getNama());
        return organisasi;
    }

}
