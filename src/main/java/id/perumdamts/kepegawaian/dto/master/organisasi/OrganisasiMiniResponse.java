package id.perumdamts.kepegawaian.dto.master.organisasi;

import id.perumdamts.kepegawaian.entities.master.Organisasi;

public record OrganisasiMiniResponse(Long id, String kode, String nama, String shortName) {
    public static OrganisasiMiniResponse from(Organisasi organisasi) {
        if (organisasi == null) return null;
        return new OrganisasiMiniResponse(
                organisasi.getId(),
                organisasi.getKode(),
                organisasi.getNama(),
                organisasi.getShortName()
        );
    }
}
