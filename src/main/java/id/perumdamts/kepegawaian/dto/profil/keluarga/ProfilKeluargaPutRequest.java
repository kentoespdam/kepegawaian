package id.perumdamts.kepegawaian.dto.profil.keluarga;

import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.ProfilKeluarga;

public class ProfilKeluargaPutRequest extends ProfilKeluargaPostRequest {
    public static ProfilKeluarga toEntity(
            ProfilKeluargaPutRequest request,
            ProfilKeluarga entity,
            Biodata biodata,
            JenjangPendidikan pendidikan
    ) {
        entity.setBiodata(biodata);
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
