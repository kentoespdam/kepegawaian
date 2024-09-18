package id.perumdamts.kepegawaian.dto.kepegawaian.mutasi;

import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatMutasi;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class RiwayatMutasiPutRequest extends RiwayatMutasiPostRequest {
    public static RiwayatMutasi toEntity(RiwayatMutasi entity, RiwayatSk riwayatSk, RiwayatMutasiPutRequest request) {
        entity.setRiwayatSk(riwayatSk);
        entity.setPegawai(riwayatSk.getPegawai());
        entity.setTmtBerlaku(request.getTmtBerlaku());
        entity.setTglBerakhir(request.getTglBerakhir());
        entity.setJenisMutasi(request.getJenisMutasi());
        entity.setNotes(request.getNotes());
        return entity;
    }

    public static RiwayatMutasi toEntity(
            RiwayatMutasi riwayatMutasi,
            RiwayatSk riwayatSk,
            RiwayatMutasiPutRequest request,
            Organisasi organisasi,
            Jabatan jabatan,
            Organisasi organisasiLama,
            Jabatan jabatanLama
    ) {
        RiwayatMutasi entity = toEntity(riwayatMutasi, riwayatSk, request);

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


    public static RiwayatMutasi toEntity(
            RiwayatMutasi riwayatMutasi,
            RiwayatSk riwayatSk,
            RiwayatMutasiPutRequest request,
            Golongan golonganBaru,
            Golongan golonganLama
    ) {
        RiwayatMutasi entity = toEntity(riwayatMutasi, riwayatSk, request);
        entity.setGolongan(golonganBaru);
        entity.setNamaGolongan(golonganBaru.getGolongan() + " - " + golonganBaru.getPangkat());
        entity.setGolonganLama(golonganLama);
        entity.setNamaGolonganLama(golonganLama.getGolongan() + " - " + golonganLama.getPangkat());

        return riwayatMutasi;
    }

    public static RiwayatMutasi toEntity(
            RiwayatMutasi riwayatMutasi,
            RiwayatSk riwayatSk,
            RiwayatMutasiPutRequest request,
            Organisasi organisasiBaru,
            Jabatan jabatanBaru,
            Profesi profesiBaru,
            Organisasi organisasiLama,
            Jabatan jabatanLama,
            Profesi profesiLama
    ) {
        RiwayatMutasi entity = toEntity(riwayatMutasi, riwayatSk, request);
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
