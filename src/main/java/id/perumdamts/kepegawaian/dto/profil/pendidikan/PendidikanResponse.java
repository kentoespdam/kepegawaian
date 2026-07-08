package id.perumdamts.kepegawaian.dto.profil.pendidikan;

import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanResponse;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataMiniResponse;
import id.perumdamts.kepegawaian.entities.profil.Pendidikan;

public record PendidikanResponse(
        Long id,
        BiodataMiniResponse biodata,
        JenjangPendidikanResponse jenjangPendidikan,
        String gelarDepan,
        String gelarBelakang,
        String jurusan,
        String institusi,
        String kota,
        Integer tahunMasuk,
        Boolean isLulus,
        Integer tahunLulus,
        Double gpa,
        Boolean isLatest,
        Boolean changedStatus
) {
    public static PendidikanResponse from(Pendidikan entity) {
        return new PendidikanResponse(
                entity.getId(),
                BiodataMiniResponse.from(entity.getBiodata()),
                JenjangPendidikanResponse.from(entity.getJenjangPendidikan()),
                entity.getGelarDepan(),
                entity.getGelarBelakang(),
                entity.getJurusan(),
                entity.getInstitusi(),
                entity.getKota(),
                entity.getTahunMasuk(),
                entity.getIsLulus(),
                entity.getTahunLulus(),
                entity.getGpa(),
                entity.getIsLatest(),
                entity.getChangedStatus()
        );
    }
}
