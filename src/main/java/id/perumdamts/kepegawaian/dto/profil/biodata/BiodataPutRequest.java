package id.perumdamts.kepegawaian.dto.profil.biodata;

import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;

public class BiodataPutRequest extends BiodataPostRequest {
    public static Biodata toEntity(BiodataPutRequest request, Biodata entity, JenjangPendidikan pendidikanTerakhir) {
        entity.setNik(request.getNik());
        entity.setNama(request.getNama());
        entity.setTempatLahir(request.getTempatLahir());
        entity.setTanggalLahir(request.getTanggalLahir());
        entity.setAlamat(request.getAlamat());
        entity.setTelp(request.getTelp());
        entity.setAgama(request.getAgama());
        entity.setIbuKandung(request.getIbuKandung());
        entity.setPendidikanTerakhir(pendidikanTerakhir);
        entity.setGolonganDarah(request.getGolonganDarah());
        entity.setStatusKawin(request.getStatusKawin());
        entity.setNotes(request.getNotes());
        return entity;
    }
}
