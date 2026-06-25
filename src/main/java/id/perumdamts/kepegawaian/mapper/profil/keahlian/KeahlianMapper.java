package id.perumdamts.kepegawaian.mapper.profil.keahlian;

import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianPostRequest;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianPutRequest;
import id.perumdamts.kepegawaian.entities.master.JenisKeahlian;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.Keahlian;

import java.time.LocalDateTime;

public final class KeahlianMapper {
    private KeahlianMapper() {}

    public static Keahlian toEntity(KeahlianPostRequest request, Biodata biodata, JenisKeahlian jenisKeahlian) {
        Keahlian entity = new Keahlian();
        entity.setBiodata(biodata);
        entity.setJenisKeahlian(jenisKeahlian);
        entity.setKualifikasi(request.getKualifikasi());
        entity.setSertifikasi(request.getSertifikasi());
        entity.setInstitusi(request.getInstitusi());
        entity.setTahun(request.getTahun());
        entity.setMasaBerlaku(request.getMasaBerlaku());
        entity.setDisetujui(true);
        entity.setTanggalPengajuan(LocalDateTime.now());
        return entity;
    }

    public static Keahlian updateEntity(Keahlian entity, KeahlianPutRequest request, Biodata biodata, JenisKeahlian jenisKeahlian) {
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
