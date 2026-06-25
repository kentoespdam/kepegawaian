package id.perumdamts.kepegawaian.mapper.profil.kartuIdentitas;

import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasPostRequest;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasPutRequest;
import id.perumdamts.kepegawaian.entities.master.JenisKitas;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.KartuIdentitas;

public final class KartuIdentitasMapper {
    private KartuIdentitasMapper() {}

    public static KartuIdentitas toEntity(KartuIdentitasPostRequest request, Biodata biodata, JenisKitas jenisKartu) {
        KartuIdentitas entity = new KartuIdentitas();
        entity.setBiodata(biodata);
        entity.setJenisKartu(jenisKartu);
        entity.setNomorKartu(request.getNomorKartu());
        entity.setTanggalExpired(request.getTanggalExpired());
        entity.setTanggalTerima(request.getTanggalTerima());
        entity.setNotes(request.getNotes());
        return entity;
    }

    public static KartuIdentitas updateEntity(KartuIdentitas entity, KartuIdentitasPutRequest request, Biodata biodata, JenisKitas jenisKartu) {
        entity.setBiodata(biodata);
        entity.setJenisKartu(jenisKartu);
        entity.setNomorKartu(request.getNomorKartu());
        entity.setTanggalExpired(request.getTanggalExpired());
        entity.setTanggalTerima(request.getTanggalTerima());
        entity.setNotes(request.getNotes());
        return entity;
    }
}
