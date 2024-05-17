package id.perumdamts.kepegawaian.dto.master.jabatan;

import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiResponse;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import lombok.Data;

import java.util.Objects;

@Data
public class JabatanResponse {
    private Long id;
    private JabatanResponse parent;
    private OrganisasiResponse organisasi;
    private LevelResponse level;
    private String nama;

    public static JabatanResponse from(Jabatan entity) {
        JabatanResponse response = new JabatanResponse();
        response.setId(entity.getId());
        if (Objects.nonNull(entity.getParent())) {
            JabatanResponse parent = getJabatanParent(entity.getParent());
            response.setParent(parent);
        }
        response.setOrganisasi(OrganisasiResponse.from(entity.getOrganisasi()));
        response.setLevel(LevelResponse.from(entity.getLevel()));
        response.setNama(entity.getNama());
        return response;
    }

    public static JabatanResponse getJabatanParent(Jabatan entity) {
        JabatanResponse response = new JabatanResponse();
        response.setId(entity.getId());
        response.setOrganisasi(OrganisasiResponse.from(entity.getOrganisasi()));
        response.setLevel(LevelResponse.from(entity.getLevel()));
        response.setNama(entity.getNama());
        return response;
    }
}
