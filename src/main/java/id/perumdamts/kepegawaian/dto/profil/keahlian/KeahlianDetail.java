package id.perumdamts.kepegawaian.dto.profil.keahlian;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;

import java.util.List;

public record KeahlianDetail(
        KeahlianQuery query,
        List<LampiranRow> lampiran
) {}
