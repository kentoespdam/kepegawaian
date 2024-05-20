package id.perumdamts.kepegawaian.dto.profil.keahlian;

import id.perumdamts.kepegawaian.entities.master.JenisKeahlian;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.Keahlian;

import java.time.LocalDateTime;

public class KeahlianPutRequest extends KeahlianPostRequest {
    public static Keahlian toEntity(KeahlianPutRequest request, Keahlian entity, Biodata biodata, JenisKeahlian jenisKeahlian) {
        entity.setBiodata(biodata);
        entity.setJenisKeahlian(jenisKeahlian);
        entity.setKualifikasi(request.getKualifikasi());
        entity.setSertifikasi(request.getSertifikasi());
        entity.setInstitusi(request.getInstitusi());
        entity.setTahun(request.getTahun());
        entity.setMasaBerlaku(request.getMasaBerlaku());
        entity.setDisetujui(false);
        entity.setTanggalPengajuan(LocalDateTime.now());
        return entity;
    }
}
