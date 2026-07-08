package id.perumdamts.kepegawaian.mapper.kepegawaian.terminasi;

import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiPutRequest;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatTerminasi;
import id.perumdamts.kepegawaian.entities.master.AlasanBerhenti;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;

import java.util.Objects;

public final class RiwayatTerminasiMapper {
    private RiwayatTerminasiMapper() {}

    public static RiwayatTerminasi toEntity(RiwayatTerminasiPostRequest request, AlasanBerhenti alasanTerminasi,
                                             RiwayatSk riwayatSk, Golongan golongan,
                                             Jabatan jabatan, Organisasi organisasi) {
        RiwayatTerminasi entity = new RiwayatTerminasi();
        entity.setAlasanTerminasi(alasanTerminasi);
        entity.setNipam(request.getNipam());
        entity.setNama(request.getNama());
        entity.setNomorSk(request.getNomorSk());
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
        entity.setTanggalTerminasi(request.getTmtBerlaku());
        entity.setTahunTerminasi(request.getTmtBerlaku().getYear());
        var tmtKerja = riwayatSk.getPegawai().getTmtKerja();
        Integer masaKerja = request.getTmtBerlaku().getYear() - tmtKerja.getYear();
        entity.setMasaKerja(masaKerja);
        entity.setNotes(request.getNotes());
        return entity;
    }

    public static RiwayatTerminasi updateEntity(RiwayatTerminasiPutRequest request, RiwayatTerminasi entity,
                                                 AlasanBerhenti alasanTerminasi, RiwayatSk riwayatSk,
                                                 Golongan golongan, Jabatan jabatan, Organisasi organisasi) {
        entity.setAlasanTerminasi(alasanTerminasi);
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
