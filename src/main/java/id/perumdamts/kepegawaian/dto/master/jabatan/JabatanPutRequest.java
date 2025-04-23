package id.perumdamts.kepegawaian.dto.master.jabatan;

import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class JabatanPutRequest extends JabatanPostRequest {
    public static Jabatan toEntity(Jabatan entity, JabatanPutRequest request, Jabatan parent, Organisasi organisasi, Level level) {
        entity.setKode(request.getKode());
        entity.setParent(parent);
        entity.setOrganisasi(organisasi);
        entity.setLevel(level);
        entity.setNama(request.getNama());
        return entity;

    }
}
