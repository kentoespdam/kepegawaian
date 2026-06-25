package id.perumdamts.kepegawaian.mapper.profil.pendidikan;

import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanPostRequest;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanPutRequest;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.Pendidikan;

public final class PendidikanMapper {
    private PendidikanMapper() {}

    public static Pendidikan toEntity(PendidikanPostRequest request, Biodata biodata, JenjangPendidikan jenjangPendidikan) {
        Pendidikan entity = new Pendidikan();
        entity.setBiodata(biodata);
        entity.setJenjangPendidikan(jenjangPendidikan);
        entity.setGelarDepan(request.getGelarDepan());
        entity.setGelarBelakang(request.getGelarBelakang());
        entity.setJurusan(request.getJurusan());
        entity.setInstitusi(request.getInstitusi());
        entity.setKota(request.getKota());
        entity.setTahunMasuk(request.getTahunMasuk());
        entity.setIsLulus(request.getIsLulus());
        entity.setTahunLulus(request.getTahunLulus());
        entity.setGpa(request.getGpa());
        entity.setIsLatest(request.getIsLatest());
        return entity;
    }

    public static Pendidikan updateEntity(Pendidikan entity, PendidikanPutRequest request, Biodata biodata, JenjangPendidikan jenjangPendidikan) {
        entity.setBiodata(biodata);
        entity.setJenjangPendidikan(jenjangPendidikan);
        entity.setGelarDepan(request.getGelarDepan());
        entity.setGelarBelakang(request.getGelarBelakang());
        entity.setJurusan(request.getJurusan());
        entity.setInstitusi(request.getInstitusi());
        entity.setKota(request.getKota());
        entity.setTahunMasuk(request.getTahunMasuk());
        entity.setIsLulus(request.getIsLulus());
        entity.setTahunLulus(request.getTahunLulus());
        entity.setGpa(request.getGpa());
        entity.setIsLatest(request.getIsLatest());
        return entity;
    }
}
