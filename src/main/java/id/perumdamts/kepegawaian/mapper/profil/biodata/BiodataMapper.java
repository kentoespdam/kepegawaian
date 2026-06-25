package id.perumdamts.kepegawaian.mapper.profil.biodata;

import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataPatchRequest;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataPostRequest;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataPutRequest;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;

public final class BiodataMapper {
    private BiodataMapper() {}

    public static Biodata toEntity(BiodataPostRequest request, JenjangPendidikan pendidikanTerakhir) {
        Biodata entity = new Biodata();
        entity.setNik(request.getNik());
        entity.setNama(request.getNama());
        entity.setJenisKelamin(request.getJenisKelamin());
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
        entity.setIsPegawai(request.getIsPegawai());
        return entity;
    }

    public static Biodata updateEntity(Biodata entity, BiodataPutRequest request, JenjangPendidikan pendidikanTerakhir) {
        entity.setNik(request.getNik());
        entity.setNama(request.getNama());
        entity.setJenisKelamin(request.getJenisKelamin());
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

    public static Biodata patchEntity(Biodata entity, BiodataPatchRequest request) {
        entity.setNama(request.getNama());
        entity.setAlamat(request.getAlamat());
        entity.setJenisKelamin(request.getJenisKelamin());
        entity.setStatusKawin(request.getStatusKawin());
        entity.setAgama(request.getAgama());
        entity.setTempatLahir(request.getTempatLahir());
        entity.setTanggalLahir(request.getTanggalLahir());
        entity.setIbuKandung(request.getIbuKandung());
        entity.setTelp(request.getTelp());
        return entity;
    }
}
