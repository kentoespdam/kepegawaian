package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk;

import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiPutRequest;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;

public class RiwayatSkPutRequest extends RiwayatSkPostRequest {
    public static RiwayatSk toEntity(RiwayatSk entity, RiwayatSkPostRequest request, Pegawai pegawai, Golongan golongan) {
        entity.setPegawai(pegawai);
        entity.setNipam(pegawai.getNipam());
        entity.setNama(pegawai.getBiodata().getNama());
        entity.setNomorSk(request.getNomorSk());
        entity.setJenisSk(request.getJenisSk());
        entity.setTanggalSk(request.getTanggalSk());
        entity.setTmtBerlaku(request.getTmtBerlaku());
        entity.setGolongan(golongan);
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

    public static RiwayatSk toEntity(RiwayatSk entity, RiwayatMutasiPutRequest request, Pegawai pegawai) {
        entity.setPegawai(pegawai);
        entity.setNipam(pegawai.getNipam());
        entity.setNama(pegawai.getBiodata().getNama());
        entity.setNomorSk(request.getNomorSk());
        entity.setJenisSk(request.getJenisSk());
        entity.setTmtBerlaku(request.getTmtBerlaku());
        entity.setUpdateMaster(request.getUpdateMaster());
        entity.setNotes(request.getNotes());
        return entity;
    }
}
