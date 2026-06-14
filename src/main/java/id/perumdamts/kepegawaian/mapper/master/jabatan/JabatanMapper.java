package id.perumdamts.kepegawaian.services.master.jabatan;

import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanPostRequest;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.master.Organisasi;

import java.util.Objects;

public final class JabatanMapper {
    private JabatanMapper() {}

    public static Jabatan toEntity(JabatanPostRequest request, Jabatan parent, Organisasi organisasi, Level level) {
        Jabatan entity = new Jabatan();
        entity.setKode(request.getKode());
        if (Objects.nonNull(parent))
            entity.setParent(parent);
        if (Objects.nonNull(organisasi))
            entity.setOrganisasi(organisasi);
        if (Objects.nonNull(level))
            entity.setLevel(level);
        entity.setNama(request.getNama());
        return entity;
    }

    public static void updateEntity(Jabatan entity, JabatanPostRequest request, Jabatan parent, Organisasi organisasi, Level level) {
        entity.setKode(request.getKode());
        if (parent != null)
            entity.setParent(parent);
        if (organisasi != null)
            entity.setOrganisasi(organisasi);
        if (level != null)
            entity.setLevel(level);
        entity.setNama(request.getNama());
    }
}
