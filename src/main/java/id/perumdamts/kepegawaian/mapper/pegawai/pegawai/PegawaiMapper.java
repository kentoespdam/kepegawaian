package id.perumdamts.kepegawaian.mapper.pegawai.pegawai;

import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiPatchGaji;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiPatchProfil;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiPostRequest;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiPutRequest;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.master.*;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPendapatanNonPajak;
import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;
import id.perumdamts.kepegawaian.entities.profil.Biodata;

import java.time.LocalDate;
import java.util.Objects;

public final class PegawaiMapper {
    private PegawaiMapper() {
    }

    // ========== Entity ← DTO (write-side) ==========

    public static Pegawai toEntity(
            PegawaiPostRequest request,
            Biodata biodata,
            Jabatan jabatan,
            Organisasi organisasi,
            Profesi profesi,
            Golongan golongan,
            GajiPendapatanNonPajak pendapatanNonPajak
    ) {
        LocalDate pensiun = biodata.getTanggalLahir().plusYears(56);
        pensiun = LocalDate.of(pensiun.getYear(), pensiun.getMonth(), 1);

        Pegawai entity = new Pegawai();
        entity.setNipam(request.getNipam());
        entity.setBiodata(biodata);
        entity.setStatusPegawai(request.getStatusPegawai());
        entity.setJabatan(jabatan);
        entity.setOrganisasi(organisasi);
        if (Objects.nonNull(profesi)) {
            entity.setProfesi(profesi);
            entity.setGrade(profesi.getGrade());
        }
        if (!request.getStatusPegawai().equals(EStatusPegawai.KONTRAK))
            entity.setGolongan(golongan);
        entity.setKodePajak(pendapatanNonPajak);
        entity.setStatusKerja(EStatusKerja.KARYAWAN_AKTIF);
        entity.setTmtKerja(request.getTmtBerlakuSk());
        if (request.getStatusPegawai().equals(EStatusPegawai.KONTRAK))
            entity.setTmtPensiun(request.getTmtKontrakSelesai());
        else
            entity.setTmtPensiun(pensiun);
        entity.setGajiPokok(request.getGajiPokok());
        entity.setEmail(request.getEmail());
        entity.setNotes(request.getNotes());

        return entity;
    }

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

    public static Pegawai toEntity(
            Pegawai entity,
            PegawaiPatchGaji request,
            GajiPendapatanNonPajak kodePajak,
            GajiProfil gajiProfil,
            RumahDinas rumahDinas
    ) {
        entity.setTmtKerja(request.getTmtKerja());
        entity.setTmtPensiun(request.getTmtPensiun());
        entity.setStatusPegawai(request.getStatusPegawai());
        entity.setGajiPokok(request.getGajiPokok());
        entity.setPhdp(request.getPhdp());
        entity.setIsAskes(request.getIsAskes());
        entity.setKodePajak(kodePajak);
        entity.setGajiProfil(gajiProfil);
        if (rumahDinas != null) entity.setRumahDinas(rumahDinas);
        return entity;
    }

    public static Pegawai toEntity(
            Pegawai entity,
            PegawaiPatchProfil request,
            Golongan golongan,
            Organisasi organisasi,
            Jabatan jabatan,
            Profesi profesi
    ) {
        Biodata biodata = getBiodata(entity, request);

        entity.setNipam(request.getNipam());
        entity.setBiodata(biodata);
        if (Objects.nonNull(golongan))
            entity.setGolongan(golongan);
        entity.setOrganisasi(organisasi);
        entity.setJabatan(jabatan);
        if (Objects.nonNull(profesi)) {
            entity.setProfesi(profesi);
            entity.setGrade(profesi.getGrade());
        }
        entity.setEmail(request.getEmail());
        entity.setAbsensiId(request.getAbsensiId());
        return entity;
    }

    private static Biodata getBiodata(Pegawai entity, PegawaiPatchProfil request) {
        Biodata biodata = entity.getBiodata();
        biodata.setNama(request.getNama());
        biodata.setJenisKelamin(request.getJenisKelamin());
        biodata.setStatusKawin(request.getStatusKawin());
        biodata.setAgama(request.getAgama());
        biodata.setTempatLahir(request.getTempatLahir());
        biodata.setTanggalLahir(request.getTanggalLahir());
        biodata.setAlamat(request.getAlamat());
        biodata.setIbuKandung(request.getIbuKandung());
        biodata.setTelp(request.getTelp());
        return biodata;
    }
}
