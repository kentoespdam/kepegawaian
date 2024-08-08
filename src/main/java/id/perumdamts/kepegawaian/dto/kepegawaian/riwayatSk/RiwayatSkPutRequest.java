package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk;

import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;

import java.util.Objects;

public class RiwayatSkPutRequest extends RiwayatSkPostRequest {
    public static RiwayatSk toEntity(RiwayatSk entity, RiwayatSkPostRequest request, Pegawai pegawai, Golongan golongan) {
        entity.setPegawai(pegawai);
        entity.setJenisSk(request.getJenisSk());
        if (Objects.nonNull(golongan)) entity.setGolongan(golongan);
        entity.setGajiPokok(request.getGajiPokok());
        entity.setMkgTahun(request.getMkgTahun());
        entity.setMkgBulan(request.getMkgBulan());
        entity.setKenaikanBerikutnya(request.getKenaikanBerikutnya());
        entity.setMkgbTahun(request.getMkgbTahun());
        entity.setMkgbBulan(request.getMkgbBulan());
        entity.setUpdateMaster(request.getUpdateMaster());
        entity.setNotes(request.getNotes());
        return entity;
    }
}
