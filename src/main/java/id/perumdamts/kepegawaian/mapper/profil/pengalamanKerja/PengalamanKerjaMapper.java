package id.perumdamts.kepegawaian.mapper.profil.pengalamanKerja;

import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.PengalamanKerjaPostRequest;
import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.PengalamanKerjaPutRequest;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.PengalamanKerja;

import java.time.LocalDateTime;

public final class PengalamanKerjaMapper {
    private PengalamanKerjaMapper() {}

    public static PengalamanKerja toEntity(PengalamanKerjaPostRequest request, Biodata biodata) {
        PengalamanKerja entity = new PengalamanKerja();
        entity.setBiodata(biodata);
        entity.setNamaPerusahaan(request.getNamaPerusahaan());
        entity.setTypePerusahaan(request.getTypePerusahaan());
        entity.setJabatan(request.getJabatan());
        entity.setLokasi(request.getLokasi());
        entity.setTahunMasuk(request.getTahunMasuk());
        entity.setTahunKeluar(request.getTahunKeluar());
        entity.setNotes(request.getNotes());
        entity.setDisetujui(true);
        entity.setTanggalPengajuan(LocalDateTime.now());
        return entity;
    }

    public static PengalamanKerja updateEntity(PengalamanKerja entity, PengalamanKerjaPutRequest request, Biodata biodata) {
        entity.setBiodata(biodata);
        entity.setNamaPerusahaan(request.getNamaPerusahaan());
        entity.setTypePerusahaan(request.getTypePerusahaan());
        entity.setJabatan(request.getJabatan());
        entity.setLokasi(request.getLokasi());
        entity.setTahunMasuk(request.getTahunMasuk());
        entity.setTahunKeluar(request.getTahunKeluar());
        entity.setNotes(request.getNotes());
        entity.setDisetujui(false);
        entity.setTanggalPengajuan(LocalDateTime.now());
        return entity;
    }
}
