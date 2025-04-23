package id.perumdamts.kepegawaian.dto.master.organisasi;

import id.perumdamts.kepegawaian.entities.master.Organisasi;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrganisasiPutRequest extends OrganisasiPostRequest {
    public static Organisasi toEntity(Organisasi entity, OrganisasiPutRequest request, Organisasi parent) {
        entity.setKode(request.getKode());
        if (parent != null)
            entity.setParent(parent);
        entity.setLevelOrg(request.getLevelOrganisasi());
        entity.setNama(request.getNama());
        entity.setShortName(request.getShortName());
        return entity;
    }
}
