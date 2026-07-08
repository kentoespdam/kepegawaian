package id.perumdamts.kepegawaian.mapper.kepegawaian.riwayatMutasi;

import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiPutRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatMutasi;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatTerminasi;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;

import java.util.Objects;

public final class RiwayatMutasiMapper {
    private RiwayatMutasiMapper() {}

    public static RiwayatMutasi toEntity(RiwayatMutasiPostRequest request, RiwayatSk riwayatSk) {
        RiwayatMutasi entity = new RiwayatMutasi();
        entity.setNipam(riwayatSk.getNipam());
        entity.setNama(riwayatSk.getNama());
        entity.setRiwayatSk(riwayatSk);
        entity.setPegawai(riwayatSk.getPegawai());
        entity.setTmtBerlaku(request.getTmtBerlaku());
        entity.setTanggalBerakhir(request.getTanggalBerakhir());
        entity.setJenisMutasi(request.getJenisMutasi());
        entity.setNotes(request.getNotes());
        return entity;
    }

    public static RiwayatMutasi toEntity(RiwayatMutasiPostRequest request, RiwayatSk riwayatSk,
                                          Golongan golongan, Golongan golonganLama) {
        RiwayatMutasi entity = toEntity(request, riwayatSk);
        entity.setGolongan(golongan);
        entity.setNamaGolongan(golongan.getPangkat() + " - " + golongan.getGolongan());
        entity.setGolonganLama(golonganLama);
        entity.setNamaGolonganLama(golonganLama.getPangkat() + " - " + golonganLama.getGolongan());
        return entity;
    }

    public static RiwayatMutasi toEntity(RiwayatMutasiPostRequest request, RiwayatSk riwayatSk,
                                          Organisasi organisasi, Jabatan jabatan, Profesi profesi,
                                          Organisasi organisasiLama, Jabatan jabatanLama, Profesi profesiLama) {
        RiwayatMutasi entity = toEntity(request, riwayatSk);
        if (Objects.nonNull(riwayatSk.getGolongan())) {
            entity.setGolongan(riwayatSk.getGolongan());
            entity.setNamaGolongan(riwayatSk.getGolongan().getPangkat() + " - " + riwayatSk.getGolongan().getGolongan());
            entity.setGolonganLama(riwayatSk.getGolongan());
            entity.setNamaGolonganLama(riwayatSk.getGolongan().getPangkat() + " - " + riwayatSk.getGolongan().getGolongan());
        }

        entity.setOrganisasi(organisasi);
        entity.setNamaOrganisasi(organisasi.getNama());
        entity.setJabatan(jabatan);
        entity.setNamaJabatan(jabatan.getNama());
        entity.setProfesi(profesi);
        entity.setNamaProfesi(profesi.getNama());

        entity.setOrganisasiLama(organisasiLama);
        entity.setNamaOrganisasiLama(organisasiLama.getNama());
        entity.setJabatanLama(jabatanLama);
        entity.setNamaJabatanLama(jabatanLama.getNama());
        entity.setProfesiLama(profesiLama);
        entity.setNamaProfesiLama(profesiLama.getNama());
        return entity;
    }

    public static RiwayatMutasi toEntity(RiwayatTerminasi riwayatTerminasi) {
        RiwayatMutasi entity = new RiwayatMutasi();
        Pegawai pegawai = riwayatTerminasi.getPegawai();
        entity.setPegawai(pegawai);
        entity.setNipam(pegawai.getNipam());
        entity.setNama(pegawai.getBiodata().getNama());
        entity.setTmtBerlaku(riwayatTerminasi.getTanggalTerminasi());
        entity.setTanggalBerakhir(riwayatTerminasi.getTanggalTerminasi());
        entity.setJenisMutasi(EJenisMutasi.TERMINASI);
        entity.setNotes(riwayatTerminasi.getNotes());

        entity.setOrganisasiLama(pegawai.getOrganisasi());
        entity.setNamaOrganisasiLama(pegawai.getOrganisasi().getNama());
        entity.setJabatanLama(pegawai.getJabatan());
        entity.setNamaJabatanLama(pegawai.getJabatan().getNama());
        entity.setProfesiLama(pegawai.getProfesi());
        entity.setNamaProfesiLama(pegawai.getProfesi().getNama());
        return entity;
    }

    public static RiwayatMutasi updateEntity(RiwayatMutasi entity, RiwayatSk riwayatSk, RiwayatMutasiPutRequest request) {
        entity.setRiwayatSk(riwayatSk);
        entity.setPegawai(riwayatSk.getPegawai());
        entity.setTmtBerlaku(request.getTmtBerlaku());
        entity.setTanggalBerakhir(request.getTanggalBerakhir());
        entity.setJenisMutasi(request.getJenisMutasi());
        entity.setNotes(request.getNotes());
        return entity;
    }

    public static RiwayatMutasi updateEntity(RiwayatMutasi riwayatMutasi, RiwayatSk riwayatSk,
                                              RiwayatMutasiPutRequest request,
                                              Golongan golonganBaru, Golongan golonganLama) {
        RiwayatMutasi entity = updateEntity(riwayatMutasi, riwayatSk, request);
        entity.setGolongan(golonganBaru);
        entity.setNamaGolongan(golonganBaru.getGolongan() + " - " + golonganBaru.getPangkat());
        entity.setGolonganLama(golonganLama);
        entity.setNamaGolonganLama(golonganLama.getGolongan() + " - " + golonganLama.getPangkat());
        return entity;
    }

    public static RiwayatMutasi updateEntity(RiwayatMutasi riwayatMutasi, RiwayatSk riwayatSk,
                                              RiwayatMutasiPutRequest request,
                                              Organisasi organisasi, Jabatan jabatan,
                                              Organisasi organisasiLama, Jabatan jabatanLama) {
        RiwayatMutasi entity = updateEntity(riwayatMutasi, riwayatSk, request);
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
        return entity;
    }

    public static RiwayatMutasi updateEntity(RiwayatMutasi riwayatMutasi, RiwayatSk riwayatSk,
                                              RiwayatMutasiPutRequest request,
                                              Organisasi organisasiBaru, Jabatan jabatanBaru, Profesi profesiBaru,
                                              Organisasi organisasiLama, Jabatan jabatanLama, Profesi profesiLama) {
        RiwayatMutasi entity = updateEntity(riwayatMutasi, riwayatSk, request);
        entity.setOrganisasi(organisasiBaru);
        entity.setNamaOrganisasi(organisasiBaru.getNama());
        entity.setJabatan(jabatanBaru);
        entity.setNamaJabatan(jabatanBaru.getNama());
        entity.setProfesi(profesiBaru);
        entity.setNamaProfesi(profesiBaru.getNama());

        if (Objects.nonNull(organisasiLama) && Objects.nonNull(jabatanLama) && Objects.nonNull(profesiLama)) {
            entity.setOrganisasiLama(organisasiLama);
            entity.setNamaOrganisasiLama(organisasiLama.getNama());
            entity.setJabatanLama(jabatanLama);
            entity.setNamaJabatanLama(jabatanLama.getNama());
            entity.setProfesiLama(profesiLama);
            entity.setNamaProfesiLama(profesiLama.getNama());
        }
        return entity;
    }
}
