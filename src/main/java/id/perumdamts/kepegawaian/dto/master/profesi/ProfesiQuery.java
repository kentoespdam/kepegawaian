package id.perumdamts.kepegawaian.dto.master.profesi;

import id.perumdamts.kepegawaian.dto.master.grade.GradeMiniResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;

public record ProfesiQuery(
        Long id,
        String nama,
        String detail,
        String resiko,
        OrganisasiMiniResponse organisasi,
        JabatanMiniResponse jabatan,
        LevelResponse level,
        GradeMiniResponse grade
) {}
