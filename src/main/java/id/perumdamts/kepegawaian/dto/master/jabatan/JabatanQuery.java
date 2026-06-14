package id.perumdamts.kepegawaian.dto.master.jabatan;

import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import lombok.Data;

@Data
public class JabatanQuery {
    private Long id;
    private String kode;
    private Long parentId;
    private JabatanMiniResponse parent;
    private Long organisasiId;
    private OrganisasiMiniResponse organisasi;
    private Long levelId;
    private LevelResponse level;
    private String nama;
}
