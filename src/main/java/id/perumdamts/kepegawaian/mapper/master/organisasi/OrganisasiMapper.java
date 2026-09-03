package id.perumdamts.kepegawaian.mapper.master.organisasi;

import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiPostRequest;
import id.perumdamts.kepegawaian.entities.master.Organisasi;

public final class OrganisasiMapper {
    private OrganisasiMapper() {}

    public static Organisasi toEntity(OrganisasiPostRequest request, Organisasi parent) {
        Organisasi organisasi = new Organisasi();
        organisasi.setKode(request.getKode());
        if (parent != null)
            organisasi.setParent(parent);
        organisasi.setLevelOrg(request.getLevelOrganisasi());
        organisasi.setNama(request.getNama());
        organisasi.setShortName(request.getShortName());
        organisasi.setCategory(request.getCategory());
        organisasi.setGroup(request.getGroup());
        return organisasi;
    }

    public static void updateEntity(Organisasi entity, OrganisasiPostRequest request, Organisasi parent) {
        entity.setKode(request.getKode());
        if (parent != null)
            entity.setParent(parent);
        entity.setLevelOrg(request.getLevelOrganisasi());
        entity.setNama(request.getNama());
        entity.setShortName(request.getShortName());
        entity.setCategory(request.getCategory());
        entity.setGroup(request.getGroup());
    }
}
