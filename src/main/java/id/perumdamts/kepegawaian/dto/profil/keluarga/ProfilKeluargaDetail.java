package id.perumdamts.kepegawaian.dto.profil.keluarga;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;

import java.util.List;

public record ProfilKeluargaDetail(
        ProfilKeluargaQuery query,
        List<LampiranRow> lampiran
) {}