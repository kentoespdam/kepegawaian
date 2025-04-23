package id.perumdamts.kepegawaian.dto.profil.pengalamanKerja;

import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.PengalamanKerja;

import java.time.LocalDateTime;

public class PengalamanKerjaPutRequest extends PengalamanKerjaPostRequest {
    public static PengalamanKerja toEntity(PengalamanKerjaPutRequest request, PengalamanKerja entity, Biodata biodata) {
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
