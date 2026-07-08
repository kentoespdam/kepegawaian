package id.perumdamts.kepegawaian.dto.master.profesi;

import id.perumdamts.kepegawaian.entities.master.Profesi;

public record ProfesiMiniResponse(
        Long id,
        String nama
) {
    public static ProfesiMiniResponse from(Profesi profesi) {
        if (profesi == null) return null;
        return new ProfesiMiniResponse(profesi.getId(), profesi.getNama());
    }
}
