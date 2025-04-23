package id.perumdamts.kepegawaian.dto.pegawai;

import id.perumdamts.kepegawaian.entities.master.*;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPendapatanNonPajak;
import id.perumdamts.kepegawaian.entities.profil.Biodata;

import java.util.Objects;

public class PegawaiPutRequest extends PegawaiPostRequest {
    public static Pegawai toEntity(
            Pegawai entity,
            PegawaiPutRequest request,
            Biodata biodata,
            Jabatan jabatan,
            Organisasi organisasi,
            Profesi profesi,
            Golongan golongan,
            GajiPendapatanNonPajak pendapatanNonPajak
    ) {
        entity.setNipam(request.getNipam());
        entity.setBiodata(biodata);
        entity.setStatusPegawai(request.getStatusPegawai());
        entity.setJabatan(jabatan);
        entity.setOrganisasi(organisasi);
        if (Objects.nonNull(profesi)) {
            entity.setProfesi(profesi);
            entity.setGrade(profesi.getGrade());
        }
        if (Objects.nonNull(golongan))
            entity.setGolongan(golongan);
        entity.setKodePajak(pendapatanNonPajak);
        entity.setStatusKerja(request.getStatusKerja());
        entity.setTmtKerja(request.getTmtBerlakuSk());

        return entity;
    }
}
