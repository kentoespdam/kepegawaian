package id.perumdamts.kepegawaian.dto.master.jabatan;

import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiResponse;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import lombok.Data;

import java.util.Objects;

@Data
public class JabatanResponse {
    private Long id;
    private String kode;
    private JabatanMiniResponse parent;
    private OrganisasiResponse organisasi;
    private LevelResponse level;
    private String nama;

    public static JabatanResponse from(Jabatan entity) {
        JabatanResponse response = new JabatanResponse();
        response.setId(entity.getId());
        response.setKode(entity.getKode());
        if (Objects.nonNull(entity.getParent()))
            response.setParent(JabatanMiniResponse.from(entity.getParent()));
        response.setOrganisasi(OrganisasiResponse.from(entity.getOrganisasi()));
        response.setLevel(LevelResponse.from(entity.getLevel()));
        response.setNama(entity.getNama());
        return response;
    }
}
