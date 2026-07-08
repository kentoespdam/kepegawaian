package id.perumdamts.kepegawaian.dto.master.jabatan;

import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;

public record JabatanQuery(
        Long id,
        String kode,
        String nama,
        JabatanMiniResponse parent,
        OrganisasiMiniResponse organisasi,
        LevelResponse level
) {}
