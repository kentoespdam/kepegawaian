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
        // Guard null: field yang tidak dikirim pada PATCH parsial (null) tidak boleh
        // menimpa nilai lama — hindari data loss (bd kepegawaian-g2ks).
        // Konsekuensi: field tidak bisa di-reset ke NULL lewat PATCH (string: pakai "").
        if (request.getNama() != null) entity.setNama(request.getNama());
        if (request.getAlamat() != null) entity.setAlamat(request.getAlamat());
        if (request.getJenisKelamin() != null) entity.setJenisKelamin(request.getJenisKelamin());
        if (request.getStatusKawin() != null) entity.setStatusKawin(request.getStatusKawin());
        if (request.getAgama() != null) entity.setAgama(request.getAgama());
        if (request.getTempatLahir() != null) entity.setTempatLahir(request.getTempatLahir());
        if (request.getTanggalLahir() != null) entity.setTanggalLahir(request.getTanggalLahir());
        if (request.getIbuKandung() != null) entity.setIbuKandung(request.getIbuKandung());
        if (request.getTelp() != null) entity.setTelp(request.getTelp());
        return entity;
    }
}
