package id.perumdamts.kepegawaian.dto.profil.pelatihan;

import id.perumdamts.kepegawaian.entities.master.JenisPelatihan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.Pelatihan;

import java.time.LocalDateTime;

public class PelatihanPutRequest extends PelatihanPostRequest {
    public static Pelatihan toEntity(PelatihanPutRequest request, Pelatihan entity, Biodata biodata, JenisPelatihan jenisPelatihan) {
        entity.setBiodata(biodata);
        entity.setJenisPelatihan(jenisPelatihan);
        entity.setNama(request.getNama());
        entity.setNilai(request.getNilai());
        entity.setLulus(request.getLulus());
        entity.setTanggalMulai(request.getTanggalMulai());
        entity.setTanggalSelesai(request.getTanggalSelesai());
        entity.setNotes(request.getNotes());
        entity.setDisetujui(false);
        entity.setTanggalPengajuan(LocalDateTime.now());
        return entity;
    }
}
