package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp;

import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSp;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.JenisSp;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.master.Sanksi;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RiwayatSpPutRequest extends RiwayatSpPostRequest {
    public static RiwayatSp toEntity(RiwayatSp entity, RiwayatSpPutRequest request, JenisSp jenisSp, Pegawai pegawai, Jabatan jabatan, Organisasi organisasi) {
        Sanksi sanksi = jenisSp.getSanksiSp().stream().filter(s -> s.getId().equals(request.getSanksiId())).findFirst().orElse(null);
        entity.setNomorSp(request.getNomorSp());
        entity.setPegawai(pegawai);
        entity.setNipam(pegawai.getNipam());
        entity.setNama(pegawai.getBiodata().getNama());
        entity.setOrganisasi(organisasi);
        entity.setNamaOrganisasi(organisasi.getNama());
        entity.setJabatan(jabatan);
        entity.setNamaJabatan(jabatan.getNama());
        entity.setTanggalSp(request.getTanggalSp());
        entity.setJenisSp(jenisSp);
        entity.setSanksi(sanksi);
        entity.setSanksiNotes(request.getSanksiNotes());
        entity.setTanggalEksekusiSanksi(request.getTanggalEksekusiSanksi());
        entity.setTanggalMulai(request.getTanggalMulai());
        entity.setTanggalSelesai(request.getTanggalSelesai());
        entity.setNotes(request.getNotes());

        return entity;
    }
}
