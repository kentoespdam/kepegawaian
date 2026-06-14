package id.perumdamts.kepegawaian.dto.master.profesi;

import id.perumdamts.kepegawaian.dto.master.grade.GradeMiniResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import lombok.Data;

@Data
public class ProfesiQuery {
    private Long id;
    private Long organisasiId;
    private OrganisasiMiniResponse organisasi;
    private Long jabatanId;
    private JabatanMiniResponse jabatan;
    private Long levelId;
    private LevelResponse level;
    private Long gradeId;
    private GradeMiniResponse grade;
    private String nama;
    private String detail;
    private String resiko;
}
