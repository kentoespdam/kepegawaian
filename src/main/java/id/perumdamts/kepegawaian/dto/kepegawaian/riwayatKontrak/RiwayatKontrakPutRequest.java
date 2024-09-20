package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak;

import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatKontrak;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RiwayatKontrakPutRequest extends RiwayatKontrakPostRequest {
    public static RiwayatKontrak toEntity(RiwayatKontrak entity, RiwayatKontrakPutRequest request, Pegawai pegawai) {
        entity.setPegawai(pegawai);
        entity.setNipam(request.getNipam());
        entity.setNama(request.getNama());
        entity.setNomorKontrak(request.getNomorKontrak());
        entity.setTanggalSk(request.getTanggalSk());
        entity.setTanggalMulai(request.getTanggalMulai());
        entity.setTanggalSelesai(request.getTanggalSelesai());
        entity.setIsLatest(request.getIsLatest());
        entity.setNotes(request.getNotes());

        return entity;
    }
}
