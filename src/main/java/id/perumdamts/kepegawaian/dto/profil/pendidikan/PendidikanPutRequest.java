package id.perumdamts.kepegawaian.dto.profil.pendidikan;

import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.Pendidikan;

import java.time.LocalDateTime;

public class PendidikanPutRequest extends PendidikanPostRequest {
    public static Pendidikan from(
            PendidikanPutRequest request,
            Pendidikan entity,
            Biodata biodata,
            JenjangPendidikan jenjangPendidikan
    ) {
        entity.setBiodata(biodata);
        entity.setJenjangPendidikan(jenjangPendidikan);
        entity.setGelarDepan(request.getGelarDepan());
        entity.setGelarBelakang(request.getGelarBelakang());
        entity.setJurusan(request.getJurusan());
        entity.setInstitusi(request.getInstitusi());
        entity.setKota(request.getKota());
        entity.setTahunMasuk(request.getTahunMasuk());
        entity.setTahunLulus(request.getTahunLulus());
        entity.setGpa(request.getGpa());
        entity.setIsLatest(request.getIsLatest());
        entity.setDisetujui(false);
        entity.setTanggalPengajuan(LocalDateTime.now());
        return entity;
    }
}
