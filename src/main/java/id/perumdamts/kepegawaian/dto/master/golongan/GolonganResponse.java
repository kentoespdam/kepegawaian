package id.perumdamts.kepegawaian.dto.master.golongan;

import id.perumdamts.kepegawaian.entities.master.Golongan;

public record GolonganResponse(
        Long id,
        String golongan,
        String pangkat
) {
    public static GolonganResponse from(Golongan golongan) {
        return golongan == null ? null : new GolonganResponse(golongan.getId(), golongan.getGolongan(), golongan.getPangkat());
    }
}
