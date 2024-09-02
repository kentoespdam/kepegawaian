package id.perumdamts.kepegawaian.dto.kepegawaian.mutasi;

import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatMutasi;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class RiwayatMutasiPutRequest extends RiwayatMutasiPostRequest {

    public static RiwayatMutasi toEntity(
            RiwayatMutasi entity,
            RiwayatSk riwayatSk,
            RiwayatMutasiPutRequest request,
            Organisasi organisasi,
            Jabatan jabatan,
            Organisasi organisasiLama,
            Jabatan jabatanLama
    ) {
        entity.setRiwayatSk(riwayatSk);
        entity.setPegawai(riwayatSk.getPegawai());
        entity.setOrganisasi(organisasi);
        entity.setNamaOrganisasi(organisasi.getNama());
        entity.setJabatan(jabatan);
        entity.setNamaJabatan(jabatan.getNama());
        if (Objects.nonNull(organisasiLama) && Objects.nonNull(jabatanLama)) {
            entity.setOrganisasiLama(organisasiLama);
            entity.setNamaOrganisasiLama(organisasiLama.getNama());
            entity.setJabatanLama(jabatanLama);
            entity.setNamaJabatanLama(jabatanLama.getNama());
        }
        entity.setTmtBerlaku(request.getTmtBerlaku());
        entity.setTglBerakhir(request.getTglBerakhir());
        entity.setJenisMutasi(request.getJenisMutasi());
        entity.setNotes(request.getNotes());

        return entity;
    }
}
