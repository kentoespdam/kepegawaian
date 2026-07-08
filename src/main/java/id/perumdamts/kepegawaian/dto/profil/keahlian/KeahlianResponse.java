package id.perumdamts.kepegawaian.dto.profil.keahlian;

import id.perumdamts.kepegawaian.dto.master.jenisKeahlian.JenisKeahlianResponse;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EKualifikasi;
import id.perumdamts.kepegawaian.entities.profil.Keahlian;

public record KeahlianResponse(
        Long id,
        BiodataMiniResponse biodata,
        JenisKeahlianResponse jenisKeahlian,
        EKualifikasi kualifikasi,
        Boolean sertifikasi,
        String institusi,
        Integer tahun,
        String masaBerlaku,
        Boolean disetujui,
        String disetujuiOleh
) {
    public static KeahlianResponse from(Keahlian entity) {
        return new KeahlianResponse(
                entity.getId(),
                BiodataMiniResponse.from(entity.getBiodata()),
                JenisKeahlianResponse.from(entity.getJenisKeahlian()),
                entity.getKualifikasi(),
                entity.getSertifikasi(),
                entity.getInstitusi(),
                entity.getTahun(),
                entity.getMasaBerlaku(),
                entity.getDisetujui(),
                entity.getDisetujuiOleh()
        );
    }
}
