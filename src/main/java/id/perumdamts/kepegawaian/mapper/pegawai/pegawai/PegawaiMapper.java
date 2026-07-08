package id.perumdamts.kepegawaian.mapper.pegawai.pegawai;

import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiMiniResponse;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiPatchGaji;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiPatchProfil;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiPostRequest;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiPutRequest;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiResponse;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiResponseDetail;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiResponseRingkasan;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.master.*;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPendapatanNonPajak;
import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.KartuIdentitas;
import id.perumdamts.kepegawaian.entities.profil.Pendidikan;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public final class PegawaiMapper {
    private PegawaiMapper() {
    }

    // ========== Entity → DTO (read-side) ==========

    public static PegawaiResponse toResponse(Pegawai pegawai) {
        if (pegawai == null) return null;
        return new PegawaiResponse(
                pegawai.getId(),
                pegawai.getNipam(),
                buildBiodata(pegawai),
                pegawai.getStatusPegawai(),
                pegawai.getOrganisasi() != null
                        ? new PegawaiResponse.Organisasi(
                        pegawai.getOrganisasi().getId(),
                        pegawai.getOrganisasi().getNama()
                ) : null,
                pegawai.getJabatan() != null
                        ? new PegawaiResponse.Jabatan(
                        pegawai.getJabatan().getId(),
                        pegawai.getJabatan().getNama()
                ) : null,
                pegawai.getProfesi() != null
                        ? new PegawaiResponse.Profesi(
                        pegawai.getProfesi().getId(),
                        pegawai.getProfesi().getNama()
                ) : null,
                pegawai.getGolongan() != null
                        ? new PegawaiResponse.Golongan(
                        pegawai.getGolongan().getId(),
                        pegawai.getGolongan().getGolongan(),
                        pegawai.getGolongan().getPangkat()
                ) : null,
                pegawai.getGrade() != null
                        ? new PegawaiResponse.Grade(
                        pegawai.getGrade().getId(),
                        pegawai.getGrade().getGrade()
                ) : null,
                pegawai.getStatusKerja(),
                pegawai.getRefSkCapegId(),
                pegawai.getTmtKerja(),
                pegawai.getTmtPensiun(),
                pegawai.getRefSkPegawaiId(),
                pegawai.getTmtPegawai(),
                pegawai.getRefSkGolId(),
                pegawai.getTmtGolongan(),
                pegawai.getRefSkJabatanId(),
                pegawai.getTmtJabatan(),
                pegawai.getRefSkMutasiId(),
                pegawai.getTmtMutasi(),
                pegawai.getGajiPokok(),
                pegawai.getPhdp(),
                pegawai.getJmlTanggungan(),
                pegawai.getKodePajak() != null
                        ? new PegawaiResponse.KodePajak(
                        pegawai.getKodePajak().getId(),
                        pegawai.getKodePajak().getKode(),
                        pegawai.getKodePajak().getKode()
                ) : null,
                pegawai.getIsAskes(),
                pegawai.getMkgTahun(),
                pegawai.getMkgBulan(),
                pegawai.getEmail(),
                pegawai.getAbsensiId(),
                pegawai.getNotes()
        );
    }

    public static PegawaiMiniResponse toMiniResponse(Pegawai pegawai) {
        if (pegawai == null) return null;
        return new PegawaiMiniResponse(
                pegawai.getId(),
                pegawai.getNipam(),
                pegawai.getBiodata() != null ? pegawai.getBiodata().getNama() : null,
                pegawai.getStatusPegawai() != null ? pegawai.getStatusPegawai().getValue() : null,
                pegawai.getJabatan() != null ? pegawai.getJabatan().getNama() : null,
                pegawai.getOrganisasi() != null ? pegawai.getOrganisasi().getNama() : null
        );
    }

    private static PegawaiResponse.Biodata buildBiodata(Pegawai pegawai) {
        if (pegawai.getBiodata() == null) return null;
        String gelarDepan = null;
        String gelarBelakang = null;
        if (pegawai.getBiodata().getPendidikanList() != null) {
            for (Pendidikan p : pegawai.getBiodata().getPendidikanList()) {
                if (Boolean.TRUE.equals(p.getIsLatest())) {
                    gelarDepan = p.getGelarDepan();
                    gelarBelakang = p.getGelarBelakang();
                    break;
                }
            }
        }
        return new PegawaiResponse.Biodata(
                pegawai.getBiodata().getNik(),
                pegawai.getBiodata().getNama(),
                gelarDepan,
                gelarBelakang
        );
    }

    public static PegawaiResponseRingkasan toRingkasan(Pegawai pegawai) {
        if (pegawai == null) return null;

        String jenisKelamin = pegawai.getBiodata() != null && pegawai.getBiodata().getJenisKelamin() != null
                ? (pegawai.getBiodata().getJenisKelamin() == EJenisKelamin.LAKI_LAKI ? "Laki-Laki" : "Perempuan")
                : null;
        String statusKawin = pegawai.getBiodata() != null && pegawai.getBiodata().getStatusKawin() != null
                ? pegawai.getBiodata().getStatusKawin().toString()
                : null;
        String agama = pegawai.getBiodata() != null && pegawai.getBiodata().getAgama() != null
                ? pegawai.getBiodata().getAgama().toString()
                : null;

        String pangkatGolongan = null;
        if (pegawai.getGolongan() != null) {
            pangkatGolongan = pegawai.getGolongan().getPangkat() + "-" + pegawai.getGolongan().getGolongan();
        }

        String mkg = (pegawai.getMkgTahun() != null && pegawai.getMkgBulan() != null)
                ? pegawai.getMkgTahun() + " Tahun " + pegawai.getMkgBulan() + " Bulan"
                : null;

        String grade = pegawai.getGrade() != null ? "Grade " + pegawai.getGrade().getGrade() : null;

        // Pendidikan terakhir
        String lembagaPendidikan = null;
        Integer tahunLulus = null;
        String pendidikanTerakhir = pegawai.getBiodata() != null && pegawai.getBiodata().getPendidikanTerakhir() != null
                ? pegawai.getBiodata().getPendidikanTerakhir().getNama()
                : null;
        if (pegawai.getBiodata() != null && pegawai.getBiodata().getPendidikanList() != null
                && !pegawai.getBiodata().getPendidikanList().isEmpty()) {
            var pendidikan = pegawai.getBiodata().getPendidikanList().stream()
                    .filter(Pendidikan::getIsLatest)
                    .toList();
            if (!pendidikan.isEmpty()) {
                lembagaPendidikan = pendidikan.getFirst().getInstitusi();
                tahunLulus = pendidikan.getFirst().getTahunLulus();
            }
        }

        // Kartu identitas
        String noNpwp = "";
        String noJamsostek = "";
        String noBpjs = "";
        String noIdCard = "";
        if (pegawai.getBiodata() != null && pegawai.getBiodata().getKartuIdentitas() != null
                && !pegawai.getBiodata().getKartuIdentitas().isEmpty()) {
            var list = pegawai.getBiodata().getKartuIdentitas();
            noNpwp = getIdNumber(list, "NPWP");
            noJamsostek = getIdNumber(list, "JPn");
            noBpjs = getIdNumber(list, "BPJS");
            noIdCard = getIdNumber(list, "ID Card");
        }

        return new PegawaiResponseRingkasan(
                pegawai.getId(),
                pegawai.getNipam(),
                pegawai.getBiodata() != null ? pegawai.getBiodata().getNama() : null,
                jenisKelamin,
                pegawai.getBiodata() != null ? pegawai.getBiodata().getTempatLahir() : null,
                pegawai.getBiodata() != null ? pegawai.getBiodata().getTanggalLahir() : null,
                statusKawin,
                pegawai.getBiodata() != null ? pegawai.getBiodata().getAlamat() : null,
                pegawai.getBiodata() != null ? pegawai.getBiodata().getNik() : null,
                agama,
                pegawai.getBiodata() != null ? pegawai.getBiodata().getTelp() : null,
                pegawai.getEmail() != null ? pegawai.getEmail() : "",
                pegawai.getKodePajak() != null ? pegawai.getKodePajak().getKode() : null,
                pegawai.getBiodata() != null ? pegawai.getBiodata().getIbuKandung() : null,
                pendidikanTerakhir,
                lembagaPendidikan,
                tahunLulus,
                pegawai.getStatusPegawai() != null ? pegawai.getStatusPegawai().value : null,
                pangkatGolongan,
                pegawai.getTmtGolongan(),
                mkg,
                pegawai.getOrganisasi() != null ? pegawai.getOrganisasi().getNama() : null,
                pegawai.getJabatan() != null ? pegawai.getJabatan().getNama() : null,
                pegawai.getProfesi() != null ? pegawai.getProfesi().getNama() : null,
                grade,
                pegawai.getTmtKerja(),
                pegawai.getTmtPegawai(),
                pegawai.getTmtPensiun(),
                pegawai.getIsAskes() != null ? pegawai.getIsAskes() : false,
                pegawai.getAbsensiId() != null ? pegawai.getAbsensiId().intValue() : null,
                "", noNpwp, noJamsostek, noBpjs, noIdCard
        );
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

    private static String getIdNumber(List<KartuIdentitas> list, String jenisKartu) {
        return list.stream()
                .filter(k -> k.getJenisKartu() != null && jenisKartu.equals(k.getJenisKartu().getNama()))
                .map(KartuIdentitas::getNomorKartu)
                .findFirst()
                .orElse("");
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
