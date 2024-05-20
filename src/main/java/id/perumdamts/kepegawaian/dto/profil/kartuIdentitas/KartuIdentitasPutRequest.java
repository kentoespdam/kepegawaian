package id.perumdamts.kepegawaian.dto.profil.kartuIdentitas;

import id.perumdamts.kepegawaian.entities.master.JenisKitas;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.KartuIdentitas;

public class KartuIdentitasPutRequest extends KartuIdentitasPostRequest {
    public static KartuIdentitas toEntity(
            KartuIdentitasPutRequest request,
            KartuIdentitas entity,
            Biodata biodata,
            JenisKitas jenisKartu) {
        entity.setBiodata(biodata);
        entity.setJenisKartu(jenisKartu);
        entity.setNomorKartu(request.getNomorKartu());
        entity.setTanggalExpired(request.getTanggalExpired());
        entity.setTanggalTerima(request.getTanggalTerima());
        entity.setNotes(request.getNotes());
        return entity;
    }

    public static KartuIdentitas toEntity(KartuIdentitasPutRequest request, KartuIdentitas entity, JenisKitas jenisKartu) {
        entity.setJenisKartu(jenisKartu);
        entity.setNomorKartu(request.getNomorKartu());
        entity.setTanggalExpired(request.getTanggalExpired());
        entity.setTanggalTerima(request.getTanggalTerima());
        entity.setNotes(request.getNotes());
        return entity;
    }
}
