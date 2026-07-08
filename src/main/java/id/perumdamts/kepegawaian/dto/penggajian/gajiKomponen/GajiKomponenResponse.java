package id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen;

import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiKomponen;

public record GajiKomponenResponse(
        Long id,
        Integer urut,
        GajiProfilResponse profilGaji,
        String kode,
        String nama,
        EJenisGaji jenisGaji,
        Double nilai,
        Boolean isReference,
        String formula
) {
    public static GajiKomponenResponse from(GajiKomponen gajiKomponen) {
        return new GajiKomponenResponse(
                gajiKomponen.getId(),
                gajiKomponen.getUrut(),
                GajiProfilResponse.from(gajiKomponen.getProfilGaji()),
                gajiKomponen.getKode(),
                gajiKomponen.getNama(),
                gajiKomponen.getJenisGaji(),
                gajiKomponen.getNilai(),
                gajiKomponen.getIsReference(),
                gajiKomponen.getFormula()
        );
    }
}
