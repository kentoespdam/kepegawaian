package id.perumdamts.kepegawaian.dto.kepegawaian.terminasi;

import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatTerminasi;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class RiwayatTerminasiPutRequest extends RiwayatTerminasiPostRequest {
    public static RiwayatTerminasi toEntity(RiwayatTerminasiPutRequest request, RiwayatTerminasi entity, RiwayatSk riwayatSk, Golongan golongan, Jabatan jabatan, Organisasi organisasi) {
        entity.setNipam(riwayatSk.getNipam());
        entity.setSkTerminasi(riwayatSk);
        entity.setPegawai(riwayatSk.getPegawai());
        entity.setOrganisasi(organisasi);
        entity.setNamaOrganisasi(organisasi.getNama());
        entity.setJabatan(jabatan);
        entity.setNamaJabatan(jabatan.getNama());
        if (Objects.nonNull(golongan)) {
            entity.setGolongan(golongan);
            entity.setNamaGolongan(golongan.getPangkat() + " - " + golongan.getGolongan());
        }
        entity.setNotes(request.getNotes());

        return entity;
    }
}
