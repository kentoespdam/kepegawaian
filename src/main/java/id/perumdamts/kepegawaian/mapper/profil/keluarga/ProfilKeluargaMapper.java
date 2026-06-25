package id.perumdamts.kepegawaian.mapper.profil.keluarga;

import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaPostRequest;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaPutRequest;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.ProfilKeluarga;

public final class ProfilKeluargaMapper {
    private ProfilKeluargaMapper() {}

    public static ProfilKeluarga toEntity(ProfilKeluargaPostRequest request, Biodata biodata, JenjangPendidikan pendidikan) {
        ProfilKeluarga entity = new ProfilKeluarga();
        entity.setBiodata(biodata);
        entity.setNik(request.getNik());
        entity.setNama(request.getNama());
        entity.setJenisKelamin(request.getJenisKelamin());
        entity.setAgama(request.getAgama());
        entity.setHubunganKeluarga(request.getHubunganKeluarga());
        entity.setTempatLahir(request.getTempatLahir());
        entity.setTanggalLahir(request.getTanggalLahir());
        entity.setTanggungan(request.getTanggungan());
        if (pendidikan != null) entity.setPendidikan(pendidikan);
        entity.setStatusPendidikan(request.getStatusPendidikan());
        entity.setStatusKawin(request.getStatusKawin());
        entity.setNotes(request.getNotes());
        return entity;
    }

    public static ProfilKeluarga updateEntity(ProfilKeluarga entity, ProfilKeluargaPutRequest request, JenjangPendidikan pendidikan) {
        entity.setNik(request.getNik());
        entity.setNama(request.getNama());
        entity.setJenisKelamin(request.getJenisKelamin());
        entity.setAgama(request.getAgama());
        entity.setHubunganKeluarga(request.getHubunganKeluarga());
        entity.setTempatLahir(request.getTempatLahir());
        entity.setTanggalLahir(request.getTanggalLahir());
        entity.setTanggungan(request.getTanggungan());
        entity.setPendidikan(pendidikan);
        entity.setStatusPendidikan(request.getStatusPendidikan());
        entity.setStatusKawin(request.getStatusKawin());
        entity.setNotes(request.getNotes());
        return entity;
    }
}
