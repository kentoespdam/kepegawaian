package id.perumdamts.kepegawaian.mapper.profil.pelatihan;

import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanPostRequest;
import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanPutRequest;
import id.perumdamts.kepegawaian.entities.master.JenisPelatihan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.Pelatihan;

public final class PelatihanMapper {
    private PelatihanMapper() {}

    public static Pelatihan toEntity(PelatihanPostRequest request, Biodata biodata, JenisPelatihan jenisPelatihan) {
        Pelatihan entity = new Pelatihan();
        entity.setBiodata(biodata);
        entity.setJenisPelatihan(jenisPelatihan);
        entity.setNama(request.getNama());
        entity.setLembaga(request.getLembaga());
        entity.setTanggalMulai(request.getTanggalMulai());
        entity.setTanggalSelesai(request.getTanggalSelesai());
        entity.setLulus(request.getLulus());
        entity.setNilai(request.getNilai());
        entity.setIkatanDinas(request.getIkatanDinas());
        entity.setTanggalAkhirIkatan(request.getTanggalAkhirIkatan());
        entity.setNotes(request.getNotes());
        return entity;
    }

    public static Pelatihan updateEntity(Pelatihan entity, PelatihanPutRequest request, Biodata biodata, JenisPelatihan jenisPelatihan) {
        entity.setBiodata(biodata);
        entity.setJenisPelatihan(jenisPelatihan);
        entity.setNama(request.getNama());
        entity.setLembaga(request.getLembaga());
        entity.setTanggalMulai(request.getTanggalMulai());
        entity.setTanggalSelesai(request.getTanggalSelesai());
        entity.setLulus(request.getLulus());
        entity.setNilai(request.getNilai());
        entity.setIkatanDinas(request.getIkatanDinas());
        entity.setTanggalAkhirIkatan(request.getTanggalAkhirIkatan());
        entity.setNotes(request.getNotes());
        return entity;
    }
}
