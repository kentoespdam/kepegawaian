package id.perumdamts.kepegawaian.mapper.kepegawaian.riwayatKontrak;

import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiPostRequest;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisKontrak;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatKontrak;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;

public final class RiwayatKontrakMapper {
    private RiwayatKontrakMapper() {}

    public static RiwayatKontrak toEntity(RiwayatKontrakPostRequest request, Pegawai pegawai) {
        RiwayatKontrak entity = new RiwayatKontrak();
        entity.setJenisKontrak(request.getJenisKontrak());
        entity.setPegawai(pegawai);
        entity.setNipam(request.getNipam());
        entity.setNama(request.getNama());
        entity.setNomorKontrak(request.getNomorKontrak());
        entity.setTanggalSk(request.getTanggalSk());
        entity.setTanggalMulai(request.getTanggalMulai());
        entity.setTanggalSelesai(request.getTanggalSelesai());
        entity.setOrganisasi(pegawai.getOrganisasi());
        entity.setJabatan(pegawai.getJabatan());
        entity.setIsLatest(request.getIsLatest());
        entity.setNotes(request.getNotes());
        return entity;
    }

    public static RiwayatKontrak toEntity(PegawaiPostRequest request, Pegawai pegawai) {
        RiwayatKontrak entity = new RiwayatKontrak();
        entity.setJenisKontrak(EJenisKontrak.PENGANGKATAN);
        entity.setPegawai(pegawai);
        entity.setNipam(pegawai.getNipam());
        entity.setNama(pegawai.getBiodata().getNama());
        entity.setNomorKontrak(request.getNomorSk());
        entity.setTanggalSk(request.getTanggalSk());
        entity.setTanggalMulai(request.getTmtBerlakuSk());
        entity.setTanggalSelesai(request.getTmtKontrakSelesai());
        entity.setIsLatest(true);
        entity.setNotes(request.getNotes());
        return entity;
    }

    public static RiwayatKontrak toEntity(RiwayatTerminasiPostRequest request, Pegawai pegawai) {
        RiwayatKontrak entity = new RiwayatKontrak();
        entity.setJenisKontrak(EJenisKontrak.TERMINASI);
        entity.setPegawai(pegawai);
        entity.setNipam(pegawai.getNipam());
        entity.setNama(pegawai.getBiodata().getNama());
        entity.setNomorKontrak(request.getNomorSk());
        entity.setTanggalSk(request.getTanggalSk());
        entity.setTanggalMulai(request.getTanggalSk());
        entity.setTanggalSelesai(request.getTanggalSk());
        entity.setIsLatest(true);
        entity.setNotes(request.getNotes());
        return entity;
    }

    public static RiwayatKontrak updateEntity(RiwayatKontrak entity, RiwayatKontrakPutRequest request, Pegawai pegawai) {
        entity.setJenisKontrak(request.getJenisKontrak());
        entity.setPegawai(pegawai);
        entity.setNipam(request.getNipam());
        entity.setNama(request.getNama());
        entity.setNomorKontrak(request.getNomorKontrak());
        entity.setTanggalSk(request.getTanggalSk());
        entity.setTanggalMulai(request.getTanggalMulai());
        entity.setTanggalSelesai(request.getTanggalSelesai());
        entity.setOrganisasi(pegawai.getOrganisasi());
        entity.setJabatan(pegawai.getJabatan());
        entity.setIsLatest(request.getIsLatest());
        entity.setNotes(request.getNotes());
        return entity;
    }
}
