package id.perumdamts.kepegawaian.repositories.pegawai.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkResponse;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.dto.master.grade.GradeResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.jenisKitas.JenisKitasResponse;
import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanResponse;
import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiMiniResponse;
import id.perumdamts.kepegawaian.dto.master.rumahDinas.RumahDinasResponse;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiListResponse;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiRequest;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiResponse;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiResponseDetail;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakResponse;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilResponse;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataResponse;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.*;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.GajiPendapatanNonPajak.GAJI_PENDAPATAN_NON_PAJAK;
import static id.perumdamts.kepegawaian.jooq.tables.GajiProfil.GAJI_PROFIL;
import static id.perumdamts.kepegawaian.jooq.tables.Golongan.GOLONGAN;
import static id.perumdamts.kepegawaian.jooq.tables.Grade.GRADE;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.JenisKitas.JENIS_KITAS;
import static id.perumdamts.kepegawaian.jooq.tables.JenjangPendidikan.JENJANG_PENDIDIKAN;
import static id.perumdamts.kepegawaian.jooq.tables.KartuIdentitas.KARTU_IDENTITAS;
import static id.perumdamts.kepegawaian.jooq.tables.Level.LEVEL;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;
import static id.perumdamts.kepegawaian.jooq.tables.Pendidikan.PENDIDIKAN;
import static id.perumdamts.kepegawaian.jooq.tables.Profesi.PROFESI;
import static id.perumdamts.kepegawaian.jooq.tables.RiwayatSk.RIWAYAT_SK;
import static id.perumdamts.kepegawaian.jooq.tables.RumahDinas.RUMAH_DINAS;
import static org.jooq.impl.DSL.multiset;
import static org.jooq.impl.DSL.select;

@Repository
@RequiredArgsConstructor
public class PegawaiQueryRepository {

    private final DSLContext dsl;

    private static final Map<String, Field<?>> ALLOWED_SORTS = Map.ofEntries(
            Map.entry("id", PEGAWAI.ID),
            Map.entry("nipam", PEGAWAI.NIPAM),
            Map.entry("nik", BIODATA.NIK),
            Map.entry("biodata.nik", BIODATA.NIK),
            Map.entry("nama", BIODATA.NAMA),
            Map.entry("biodata.nama", BIODATA.NAMA),
            Map.entry("statusPegawai", PEGAWAI.STATUS_PEGAWAI),
            Map.entry("jabatanId", JABATAN.NAMA),
            Map.entry("jabatan.nama", JABATAN.NAMA),
            Map.entry("organisasiId", ORGANISASI.NAMA),
            Map.entry("organisasi.nama", ORGANISASI.NAMA),
            Map.entry("profesiId", PROFESI.NAMA),
            Map.entry("profesi.nama", PROFESI.NAMA),
            Map.entry("golonganId", GOLONGAN.GOLONGAN_),
            Map.entry("golongan.golongan", GOLONGAN.GOLONGAN_),
            Map.entry("gradeId", GRADE.GRADE_),
            Map.entry("grade.grade", GRADE.GRADE_),
            Map.entry("jenisKelamin", BIODATA.JENIS_KELAMIN),
            Map.entry("biodata.jenisKelamin", BIODATA.JENIS_KELAMIN),
            Map.entry("statusKerja", PEGAWAI.STATUS_KERJA),
            Map.entry("tmtKerja", PEGAWAI.TMT_KERJA),
            Map.entry("tmtPegawai", PEGAWAI.TMT_PEGAWAI),
            Map.entry("tmtGolongan", PEGAWAI.TMT_GOLONGAN),
            Map.entry("tmtJabatan", PEGAWAI.TMT_JABATAN),
            Map.entry("tmtMutasi", PEGAWAI.TMT_MUTASI),
            Map.entry("gajiPokok", PEGAWAI.GAJI_POKOK),
            Map.entry("phdp", PEGAWAI.PHDP),
            Map.entry("isAskes", PEGAWAI.IS_ASKES),
            Map.entry("mkgTahun", PEGAWAI.MKG_TAHUN),
            Map.entry("mkgBulan", PEGAWAI.MKG_BULAN),
            Map.entry("email", PEGAWAI.EMAIL),
            Map.entry("absensiId", PEGAWAI.ABSENSI_ID)
    );

    private org.jooq.Condition buildConditions(PegawaiRequest request) {
        var conditions = DSL.trueCondition().and(PEGAWAI.IS_DELETED.eq(false));

        if (request.getNipam() != null && !request.getNipam().isBlank()) {
            conditions = conditions.and(PEGAWAI.NIPAM.containsIgnoreCase(request.getNipam()));
        }
        if (request.getNik() != null && !request.getNik().isBlank()) {
            conditions = conditions.and(BIODATA.NIK.containsIgnoreCase(request.getNik()));
        }
        if (request.getNama() != null && !request.getNama().isBlank()) {
            conditions = conditions.and(BIODATA.NAMA.containsIgnoreCase(request.getNama()));
        }
        if (request.getStatusPegawai() != null) {
            conditions = conditions.and(PEGAWAI.STATUS_PEGAWAI.eq((byte) request.getStatusPegawai().ordinal()));
        }
        if (request.getJabatanId() != null) {
            conditions = conditions.and(PEGAWAI.JABATAN_ID.eq(request.getJabatanId()));
        }
        if (request.getOrganisasiId() != null) {
            conditions = conditions.and(PEGAWAI.ORGANISASI_ID.eq(request.getOrganisasiId()));
        }
        if (request.getProfesiId() != null) {
            conditions = conditions.and(PEGAWAI.PROFESI_ID.eq(request.getProfesiId()));
        }
        if (request.getGolonganId() != null) {
            conditions = conditions.and(PEGAWAI.GOLONGAN_ID.eq(request.getGolonganId()));
        }
        if (request.getGradeId() != null) {
            conditions = conditions.and(PEGAWAI.GRADE_ID.eq(request.getGradeId()));
        }
        if (request.getStatusKerja() != null) {
            conditions = conditions.and(PEGAWAI.STATUS_KERJA.eq((byte) request.getStatusKerja().ordinal()));
        }
        if (request.getJenisKelamin() != null) {
            conditions = conditions.and(BIODATA.JENIS_KELAMIN.eq((byte) request.getJenisKelamin().ordinal()));
        }

        return conditions;
    }

    public Page<PegawaiResponse> findPage(PegawaiRequest request) {
        var conditions = buildConditions(request);
        var sortOrder = SortParam.resolve(request.getSortBy(), request.getSortDirection(), ALLOWED_SORTS, PEGAWAI.ID);

        var total = dsl.selectCount()
                .from(PEGAWAI)
                .leftJoin(BIODATA).on(PEGAWAI.NIK.eq(BIODATA.NIK))
                .where(conditions)
                .fetchOneInto(Long.class);

        var rows = dsl.select(
                        PEGAWAI.ID,
                        PEGAWAI.NIPAM,
                        PEGAWAI.STATUS_PEGAWAI,
                        PEGAWAI.STATUS_KERJA,
                        PEGAWAI.REF_SK_CAPEG_ID,
                        PEGAWAI.TMT_KERJA,
                        PEGAWAI.TMT_PENSIUN,
                        PEGAWAI.REF_SK_PEGAWAI_ID,
                        PEGAWAI.TMT_PEGAWAI,
                        PEGAWAI.REF_SK_GOL_ID,
                        PEGAWAI.TMT_GOLONGAN,
                        PEGAWAI.REF_SK_JABATAN_ID,
                        PEGAWAI.TMT_JABATAN,
                        PEGAWAI.REF_SK_MUTASI_ID,
                        PEGAWAI.TMT_MUTASI,
                        PEGAWAI.GAJI_POKOK,
                        PEGAWAI.PHDP,
                        PEGAWAI.JML_TANGGUNGAN,
                        PEGAWAI.IS_ASKES,
                        PEGAWAI.MKG_TAHUN,
                        PEGAWAI.MKG_BULAN,
                        PEGAWAI.EMAIL,
                        PEGAWAI.ABSENSI_ID,
                        PEGAWAI.NOTES,
                        BIODATA.NIK.as("biodata_nik"),
                        BIODATA.NAMA.as("biodata_nama"),
                        PENDIDIKAN.GELAR_DEPAN.as("biodata_gelar_depan"),
                        PENDIDIKAN.GELAR_BELAKANG.as("biodata_gelar_belakang"),
                        ORGANISASI.ID.as("organisasi_id"),
                        ORGANISASI.NAMA.as("organisasi_nama"),
                        JABATAN.ID.as("jabatan_id"),
                        JABATAN.NAMA.as("jabatan_nama"),
                        PROFESI.ID.as("profesi_id"),
                        PROFESI.NAMA.as("profesi_nama"),
                        GOLONGAN.ID.as("golongan_id"),
                        GOLONGAN.GOLONGAN_.as("golongan_golongan"),
                        GOLONGAN.PANGKAT.as("golongan_pangkat"),
                        GRADE.ID.as("grade_id"),
                        GRADE.GRADE_.as("grade_grade"),
                        GAJI_PENDAPATAN_NON_PAJAK.ID.as("kode_pajak_id"),
                        GAJI_PENDAPATAN_NON_PAJAK.KODE.as("kode_pajak_kode")
                )
                .from(PEGAWAI)
                .leftJoin(BIODATA).on(PEGAWAI.NIK.eq(BIODATA.NIK))
                .leftJoin(PENDIDIKAN).on(PENDIDIKAN.BIODATA_ID.eq(BIODATA.NIK)
                        .and(PENDIDIKAN.IS_LATEST.eq((byte) 1))
                        .and(PENDIDIKAN.IS_DELETED.eq(false)))
                .leftJoin(ORGANISASI).on(PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(PROFESI).on(PEGAWAI.PROFESI_ID.eq(PROFESI.ID))
                .leftJoin(GOLONGAN).on(PEGAWAI.GOLONGAN_ID.eq(GOLONGAN.ID))
                .leftJoin(GRADE).on(PEGAWAI.GRADE_ID.eq(GRADE.ID))
                .leftJoin(GAJI_PENDAPATAN_NON_PAJAK).on(PEGAWAI.GAJI_PENDAPATAN_NON_PAJAK_ID.eq(GAJI_PENDAPATAN_NON_PAJAK.ID))
                .where(conditions)
                .orderBy(sortOrder)
                .limit(request.getSizeOrDefault())
                .offset(request.offset())
                .fetch(PegawaiQueryRepository::mapPegawaiResponse);

        return new PageImpl<>(rows, PageRequest.of(request.getPageNumber(), request.getSizeOrDefault()), total != null ? total : 0L);
    }

    public List<PegawaiListResponse> findAll(PegawaiRequest request) {
        var conditions = buildConditions(request);
        var sortOrder = SortParam.resolve(request.getSortBy(), request.getSortDirection(), ALLOWED_SORTS, PEGAWAI.ID);

        return dsl.select(
                        PEGAWAI.ID,
                        PEGAWAI.NIPAM,
                        PEGAWAI.STATUS_PEGAWAI,
                        BIODATA.NAMA.as("biodata_nama"),
                        ORGANISASI.ID.as("organisasi_id"),
                        ORGANISASI.KODE.as("organisasi_kode"),
                        ORGANISASI.NAMA.as("organisasi_nama"),
                        ORGANISASI.SHORT_NAME.as("organisasi_short_name"),
                        JABATAN.ID.as("jabatan_id"),
                        JABATAN.KODE.as("jabatan_kode"),
                        JABATAN.NAMA.as("jabatan_nama"),
                        LEVEL.ID.as("level_id"),
                        LEVEL.NAMA.as("level_nama"),
                        GOLONGAN.ID.as("golongan_id"),
                        GOLONGAN.GOLONGAN_.as("golongan_golongan"),
                        GOLONGAN.PANGKAT.as("golongan_pangkat")
                )
                .from(PEGAWAI)
                .leftJoin(BIODATA).on(PEGAWAI.NIK.eq(BIODATA.NIK))
                .leftJoin(ORGANISASI).on(PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(LEVEL).on(JABATAN.LEVEL_ID.eq(LEVEL.ID))
                .leftJoin(GOLONGAN).on(PEGAWAI.GOLONGAN_ID.eq(GOLONGAN.ID))
                .where(conditions)
                .orderBy(sortOrder)
                .fetch(PegawaiQueryRepository::mapPegawaiListResponse);
    }

    public List<PegawaiListResponse> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return dsl.select(
                        PEGAWAI.ID,
                        PEGAWAI.NIPAM,
                        PEGAWAI.STATUS_PEGAWAI,
                        BIODATA.NAMA.as("biodata_nama"),
                        ORGANISASI.ID.as("organisasi_id"),
                        ORGANISASI.KODE.as("organisasi_kode"),
                        ORGANISASI.NAMA.as("organisasi_nama"),
                        ORGANISASI.SHORT_NAME.as("organisasi_short_name"),
                        JABATAN.ID.as("jabatan_id"),
                        JABATAN.KODE.as("jabatan_kode"),
                        JABATAN.NAMA.as("jabatan_nama"),
                        LEVEL.ID.as("level_id"),
                        LEVEL.NAMA.as("level_nama"),
                        GOLONGAN.ID.as("golongan_id"),
                        GOLONGAN.GOLONGAN_.as("golongan_golongan"),
                        GOLONGAN.PANGKAT.as("golongan_pangkat")
                )
                .from(PEGAWAI)
                .leftJoin(BIODATA).on(PEGAWAI.NIK.eq(BIODATA.NIK))
                .leftJoin(ORGANISASI).on(PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(LEVEL).on(JABATAN.LEVEL_ID.eq(LEVEL.ID))
                .leftJoin(GOLONGAN).on(PEGAWAI.GOLONGAN_ID.eq(GOLONGAN.ID))
                .where(PEGAWAI.ID.in(ids).and(PEGAWAI.IS_DELETED.eq(false)))
                .fetch(PegawaiQueryRepository::mapPegawaiListResponse);
    }

    public Optional<PegawaiResponse> findByNipam(String nipam) {
        if (nipam == null || nipam.isBlank()) {
            return Optional.empty();
        }
        return dsl.select(
                        PEGAWAI.ID,
                        PEGAWAI.NIPAM,
                        PEGAWAI.STATUS_PEGAWAI,
                        PEGAWAI.STATUS_KERJA,
                        PEGAWAI.REF_SK_CAPEG_ID,
                        PEGAWAI.TMT_KERJA,
                        PEGAWAI.TMT_PENSIUN,
                        PEGAWAI.REF_SK_PEGAWAI_ID,
                        PEGAWAI.TMT_PEGAWAI,
                        PEGAWAI.REF_SK_GOL_ID,
                        PEGAWAI.TMT_GOLONGAN,
                        PEGAWAI.REF_SK_JABATAN_ID,
                        PEGAWAI.TMT_JABATAN,
                        PEGAWAI.REF_SK_MUTASI_ID,
                        PEGAWAI.TMT_MUTASI,
                        PEGAWAI.GAJI_POKOK,
                        PEGAWAI.PHDP,
                        PEGAWAI.JML_TANGGUNGAN,
                        PEGAWAI.IS_ASKES,
                        PEGAWAI.MKG_TAHUN,
                        PEGAWAI.MKG_BULAN,
                        PEGAWAI.EMAIL,
                        PEGAWAI.ABSENSI_ID,
                        PEGAWAI.NOTES,
                        BIODATA.NIK.as("biodata_nik"),
                        BIODATA.NAMA.as("biodata_nama"),
                        PENDIDIKAN.GELAR_DEPAN.as("biodata_gelar_depan"),
                        PENDIDIKAN.GELAR_BELAKANG.as("biodata_gelar_belakang"),
                        ORGANISASI.ID.as("organisasi_id"),
                        ORGANISASI.NAMA.as("organisasi_nama"),
                        JABATAN.ID.as("jabatan_id"),
                        JABATAN.NAMA.as("jabatan_nama"),
                        PROFESI.ID.as("profesi_id"),
                        PROFESI.NAMA.as("profesi_nama"),
                        GOLONGAN.ID.as("golongan_id"),
                        GOLONGAN.GOLONGAN_.as("golongan_golongan"),
                        GOLONGAN.PANGKAT.as("golongan_pangkat"),
                        GRADE.ID.as("grade_id"),
                        GRADE.GRADE_.as("grade_grade"),
                        GAJI_PENDAPATAN_NON_PAJAK.ID.as("kode_pajak_id"),
                        GAJI_PENDAPATAN_NON_PAJAK.KODE.as("kode_pajak_kode")
                )
                .from(PEGAWAI)
                .leftJoin(BIODATA).on(PEGAWAI.NIK.eq(BIODATA.NIK))
                .leftJoin(PENDIDIKAN).on(PENDIDIKAN.BIODATA_ID.eq(BIODATA.NIK)
                        .and(PENDIDIKAN.IS_LATEST.eq((byte) 1))
                        .and(PENDIDIKAN.IS_DELETED.eq(false)))
                .leftJoin(ORGANISASI).on(PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(PROFESI).on(PEGAWAI.PROFESI_ID.eq(PROFESI.ID))
                .leftJoin(GOLONGAN).on(PEGAWAI.GOLONGAN_ID.eq(GOLONGAN.ID))
                .leftJoin(GRADE).on(PEGAWAI.GRADE_ID.eq(GRADE.ID))
                .leftJoin(GAJI_PENDAPATAN_NON_PAJAK).on(PEGAWAI.GAJI_PENDAPATAN_NON_PAJAK_ID.eq(GAJI_PENDAPATAN_NON_PAJAK.ID))
                .where(PEGAWAI.NIPAM.eq(nipam).and(PEGAWAI.IS_DELETED.eq(false)))
                .fetchOptional(PegawaiQueryRepository::mapPegawaiResponse);
    }

    public Optional<PegawaiResponseDetail> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        var kartuIdentitasMultiset = multiset(
                select(
                        KARTU_IDENTITAS.ID,
                        KARTU_IDENTITAS.NOMOR_KARTU,
                        JENIS_KITAS.ID.as("jenis_kartu_id"),
                        JENIS_KITAS.NAMA.as("jenis_kartu_nama")
                ).from(KARTU_IDENTITAS)
                        .leftJoin(JENIS_KITAS).on(KARTU_IDENTITAS.JENIS_KITAS_ID.eq(JENIS_KITAS.ID))
                        .where(KARTU_IDENTITAS.NIK.eq(BIODATA.NIK))
                        .and(KARTU_IDENTITAS.IS_DELETED.eq(false))
        ).as("kartu_identitas");

        var optDetail = dsl.select(
                        PEGAWAI.ID,
                        PEGAWAI.NIPAM,
                        PEGAWAI.STATUS_PEGAWAI,
                        PEGAWAI.STATUS_KERJA,
                        PEGAWAI.TMT_KERJA,
                        PEGAWAI.TMT_PENSIUN,
                        PEGAWAI.GAJI_POKOK,
                        PEGAWAI.PHDP,
                        PEGAWAI.JML_TANGGUNGAN,
                        PEGAWAI.MKG_TAHUN,
                        PEGAWAI.MKG_BULAN,
                        PEGAWAI.ABSENSI_ID,
                        PEGAWAI.EMAIL,
                        PEGAWAI.NOTES,
                        PEGAWAI.IS_ASKES,
                        PEGAWAI.TMT_PEGAWAI,
                        BIODATA.NIK.as("biodata_nik"),
                        BIODATA.NAMA.as("biodata_nama"),
                        BIODATA.JENIS_KELAMIN.as("biodata_jenis_kelamin"),
                        BIODATA.TEMPAT_LAHIR.as("biodata_tempat_lahir"),
                        BIODATA.TANGGAL_LAHIR.as("biodata_tanggal_lahir"),
                        BIODATA.ALAMAT.as("biodata_alamat"),
                        BIODATA.TELP.as("biodata_telp"),
                        BIODATA.AGAMA.as("biodata_agama"),
                        BIODATA.IBU_KANDUNG.as("biodata_ibu_kandung"),
                        BIODATA.GOLONGAN_DARAH.as("biodata_golongan_darah"),
                        BIODATA.STATUS_KAWIN.as("biodata_status_kawin"),
                        BIODATA.FOTO_PROFIL.as("biodata_foto_profil"),
                        BIODATA.NOTES.as("biodata_notes"),
                        JENJANG_PENDIDIKAN.ID.as("jenjang_id"),
                        JENJANG_PENDIDIKAN.NAMA.as("jenjang_nama"),
                        JENJANG_PENDIDIKAN.SHORT_NAME.as("jenjang_short_name"),
                        JENJANG_PENDIDIKAN.SEQ.as("jenjang_seq"),
                        JENJANG_PENDIDIKAN.IS_STATISTIK.as("jenjang_is_statistik"),
                        ORGANISASI.ID.as("organisasi_id"),
                        ORGANISASI.KODE.as("organisasi_kode"),
                        ORGANISASI.NAMA.as("organisasi_nama"),
                        ORGANISASI.SHORT_NAME.as("organisasi_short_name"),
                        JABATAN.ID.as("jabatan_id"),
                        JABATAN.KODE.as("jabatan_kode"),
                        JABATAN.NAMA.as("jabatan_nama"),
                        LEVEL.ID.as("level_id"),
                        LEVEL.NAMA.as("level_nama"),
                        PROFESI.ID.as("profesi_id"),
                        PROFESI.NAMA.as("profesi_nama"),
                        GOLONGAN.ID.as("golongan_id"),
                        GOLONGAN.GOLONGAN_.as("golongan_golongan"),
                        GOLONGAN.PANGKAT.as("golongan_pangkat"),
                        GRADE.ID.as("grade_id"),
                        GRADE.GRADE_.as("grade_grade"),
                        GRADE.TUKIN.as("grade_tukin"),
                        GRADE.LEVEL_ID.as("grade_level_id"),
                        DSL.select(LEVEL.NAMA).from(LEVEL).where(LEVEL.ID.eq(GRADE.LEVEL_ID)).asField("grade_level_nama"),
                        GAJI_PENDAPATAN_NON_PAJAK.ID.as("kode_pajak_id"),
                        GAJI_PENDAPATAN_NON_PAJAK.KODE.as("kode_pajak_kode"),
                        GAJI_PENDAPATAN_NON_PAJAK.NOMINAL.as("kode_pajak_nominal"),
                        GAJI_PENDAPATAN_NON_PAJAK.NOTES.as("kode_pajak_notes"),
                        GAJI_PROFIL.ID.as("gaji_profil_id"),
                        GAJI_PROFIL.NAMA.as("gaji_profil_nama"),
                        RUMAH_DINAS.ID.as("rumah_dinas_id"),
                        RUMAH_DINAS.NAMA.as("rumah_dinas_nama"),
                        RUMAH_DINAS.NILAI.as("rumah_dinas_nilai"),
                        kartuIdentitasMultiset
                )
                .from(PEGAWAI)
                .leftJoin(BIODATA).on(PEGAWAI.NIK.eq(BIODATA.NIK))
                .leftJoin(JENJANG_PENDIDIKAN).on(BIODATA.PENDIDIKAN_ID.eq(JENJANG_PENDIDIKAN.ID))
                .leftJoin(ORGANISASI).on(PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(LEVEL).on(JABATAN.LEVEL_ID.eq(LEVEL.ID))
                .leftJoin(PROFESI).on(PEGAWAI.PROFESI_ID.eq(PROFESI.ID))
                .leftJoin(GOLONGAN).on(PEGAWAI.GOLONGAN_ID.eq(GOLONGAN.ID))
                .leftJoin(GRADE).on(PEGAWAI.GRADE_ID.eq(GRADE.ID))
                .leftJoin(GAJI_PENDAPATAN_NON_PAJAK).on(PEGAWAI.GAJI_PENDAPATAN_NON_PAJAK_ID.eq(GAJI_PENDAPATAN_NON_PAJAK.ID))
                .leftJoin(GAJI_PROFIL).on(PEGAWAI.GAJI_PROFIL_ID.eq(GAJI_PROFIL.ID))
                .leftJoin(RUMAH_DINAS).on(PEGAWAI.RUMAH_DINAS_ID.eq(RUMAH_DINAS.ID))
                .where(PEGAWAI.ID.eq(id).and(PEGAWAI.IS_DELETED.eq(false)))
                .fetchOptional();

        if (optDetail.isEmpty()) {
            return Optional.empty();
        }

        var r = optDetail.get();
        var cards = r.get(kartuIdentitasMultiset);
        List<KartuIdentitasMiniResponse> kartuIdentitasList = new ArrayList<>();
        if (cards != null) {
            for (var cardRecord : cards) {
                KartuIdentitasMiniResponse responseCard = new KartuIdentitasMiniResponse();
                responseCard.setId(cardRecord.get("id", Long.class));
                responseCard.setNomorKartu(cardRecord.get("nomor_kartu", String.class));
                Long jenisId = cardRecord.get("jenis_kartu_id", Long.class);
                if (jenisId != null) {
                    JenisKitasResponse jk = new JenisKitasResponse();
                    jk.setId(jenisId);
                    jk.setNama(cardRecord.get("jenis_kartu_nama", String.class));
                    responseCard.setJenisKartu(jk);
                }
                kartuIdentitasList.add(responseCard);
            }
        }

        PegawaiResponseDetail response = new PegawaiResponseDetail();
        response.setId(r.get(PEGAWAI.ID));
        response.setNipam(r.get(PEGAWAI.NIPAM));

        Byte statusPegawaiByte = r.get(PEGAWAI.STATUS_PEGAWAI);
        response.setStatusPegawai(statusPegawaiByte != null ? EStatusPegawai.values()[statusPegawaiByte] : null);

        Byte statusKerjaByte = r.get(PEGAWAI.STATUS_KERJA);
        response.setStatusKerja(statusKerjaByte != null ? EStatusKerja.values()[statusKerjaByte] : null);

        response.setTmtKerja(r.get(PEGAWAI.TMT_KERJA));
        response.setTmtPensiun(r.get(PEGAWAI.TMT_PENSIUN));
        response.setGajiPokok(r.get(PEGAWAI.GAJI_POKOK));
        response.setPhdp(r.get(PEGAWAI.PHDP));
        response.setJmlTanggungan(r.get(PEGAWAI.JML_TANGGUNGAN));
        response.setMkgTahun(r.get(PEGAWAI.MKG_TAHUN));
        response.setMkgBulan(r.get(PEGAWAI.MKG_BULAN));
        response.setAbsensiId(r.get(PEGAWAI.ABSENSI_ID));
        response.setEmail(r.get(PEGAWAI.EMAIL));
        response.setNotes(r.get(PEGAWAI.NOTES));
        response.setIsAskes(r.get(PEGAWAI.IS_ASKES));

        // BiodataResponse
        String nik = r.get("biodata_nik", String.class);
        if (nik != null) {
            BiodataResponse bio = new BiodataResponse();
            bio.setNik(nik);
            bio.setNama(r.get("biodata_nama", String.class));

            Byte jkByte = r.get("biodata_jenis_kelamin", Byte.class);
            bio.setJenisKelamin(jkByte != null ? EJenisKelamin.values()[jkByte] : null);

            bio.setTempatLahir(r.get("biodata_tempat_lahir", String.class));
            bio.setTanggalLahir(r.get("biodata_tanggal_lahir", LocalDate.class));
            bio.setAlamat(r.get("biodata_alamat", String.class));
            bio.setTelp(r.get("biodata_telp", String.class));

            Byte agByte = r.get("biodata_agama", Byte.class);
            bio.setAgama(agByte != null ? EAgama.values()[agByte] : null);

            bio.setIbuKandung(r.get("biodata_ibu_kandung", String.class));

            Long jpId = r.get("jenjang_id", Long.class);
            if (jpId != null) {
                JenjangPendidikanResponse jp = new JenjangPendidikanResponse();
                jp.setId(jpId);
                jp.setNama(r.get("jenjang_nama", String.class));
                jp.setShortName(r.get("jenjang_short_name", String.class));
                jp.setSeq(r.get("jenjang_seq", Integer.class));
                jp.setIsStatistik(r.get("jenjang_is_statistik", Boolean.class));
                bio.setPendidikanTerakhir(jp);
            }

            String gdStr = r.get("biodata_golongan_darah", String.class);
            bio.setGolonganDarah(gdStr != null ? EGolonganDarah.valueOf(gdStr) : null);

            Byte skByte = r.get("biodata_status_kawin", Byte.class);
            bio.setStatusKawin(skByte != null ? EStatusKawin.values()[skByte] : null);

            bio.setFotoProfil(r.get("biodata_foto_profil", String.class));
            bio.setNotes(r.get("biodata_notes", String.class));
            bio.setKartuIdentitas(kartuIdentitasList);

            response.setBiodata(bio);
        }

        // OrganisasiMiniResponse
        Long orgId = r.get("organisasi_id", Long.class);
        if (orgId != null) {
            OrganisasiMiniResponse org = new OrganisasiMiniResponse();
            org.setId(orgId);
            org.setKode(r.get("organisasi_kode", String.class));
            org.setNama(r.get("organisasi_nama", String.class));
            org.setShortName(r.get("organisasi_short_name", String.class));
            response.setOrganisasi(org);
        }

        // JabatanMiniResponse
        Long jabId = r.get("jabatan_id", Long.class);
        if (jabId != null) {
            JabatanMiniResponse jab = new JabatanMiniResponse();
            jab.setId(jabId);
            jab.setKode(r.get("jabatan_kode", String.class));
            jab.setNama(r.get("jabatan_nama", String.class));

            Long lvlId = r.get("level_id", Long.class);
            if (lvlId != null) {
                jab.setLevel(new LevelResponse(lvlId, r.get("level_nama", String.class)));
            }
            response.setJabatan(jab);
        }

        // ProfesiMiniResponse
        Long profId = r.get("profesi_id", Long.class);
        if (profId != null) {
            ProfesiMiniResponse prof = new ProfesiMiniResponse();
            prof.setId(profId);
            prof.setNama(r.get("profesi_nama", String.class));
            response.setProfesi(prof);
        }

        // GolonganResponse
        Long golId = r.get("golongan_id", Long.class);
        if (golId != null) {
            response.setGolongan(new GolonganResponse(
                    golId,
                    r.get("golongan_golongan", String.class),
                    r.get("golongan_pangkat", String.class)
            ));
        }

        // GradeResponse
        Long grdId = r.get("grade_id", Long.class);
        if (grdId != null) {
            GradeResponse grd = new GradeResponse();
            grd.setId(grdId);
            grd.setGrade(r.get("grade_grade", Integer.class));
            grd.setTukin(r.get("grade_tukin", Double.class));

            Long glvlId = r.get("grade_level_id", Long.class);
            if (glvlId != null) {
                grd.setLevel(new LevelResponse(glvlId, r.get("grade_level_nama", String.class)));
            }
            response.setGrade(grd);
        }

        // GajiPendapatanNonPajakResponse
        Long pajId = r.get("kode_pajak_id", Long.class);
        if (pajId != null) {
            GajiPendapatanNonPajakResponse paj = new GajiPendapatanNonPajakResponse();
            paj.setId(pajId);
            paj.setKode(r.get("kode_pajak_kode", String.class));
            paj.setNominal(r.get("kode_pajak_nominal", Double.class));
            paj.setNotes(r.get("kode_pajak_notes", String.class));
            response.setKodePajak(paj);
        }

        // GajiProfilResponse
        Long gpId = r.get("gaji_profil_id", Long.class);
        if (gpId != null) {
            GajiProfilResponse gp = new GajiProfilResponse();
            gp.setId(gpId);
            gp.setNama(r.get("gaji_profil_nama", String.class));
            response.setGajiProfil(gp);
        }

        // RumahDinasResponse
        Long rdId = r.get("rumah_dinas_id", Long.class);
        if (rdId != null) {
            RumahDinasResponse rd = new RumahDinasResponse();
            rd.setId(rdId);
            rd.setNama(r.get("rumah_dinas_nama", String.class));
            rd.setNilai(r.get("rumah_dinas_nilai", Double.class));
            response.setRumahDinas(rd);
        }

        // Fetch SKs
        List<RiwayatSkResponse> skList = findRiwayatSkList(id);
        response.setTanggalSk(r.get(PEGAWAI.TMT_PEGAWAI));
        response.setSkCapeg(getLastFromResponseList(skList, EJenisSk.SK_CAPEG));
        if (response.getSkCapeg() != null) {
            response.setTanggalSk(response.getSkCapeg().getTmtBerlaku());
        }
        response.setSkPegawai(getLastFromResponseList(skList, EJenisSk.SK_PEGAWAI_TETAP));
        response.setSkGolongan(getLastFromResponseList(skList, EJenisSk.SK_KENAIKAN_PANGKAT_GOLONGAN));
        response.setSkJabatan(getLastFromResponseList(skList, EJenisSk.SK_JABATAN));
        response.setSkMutasi(getLastFromResponseList(skList, EJenisSk.SK_MUTASI));
        response.setSkKontrak(getLastFromResponseList(skList, EJenisSk.SK_LAINNYA));
        response.setSkGajiBerkala(getLastFromResponseList(skList, EJenisSk.SK_KENAIKAN_GAJI_BERKALA));

        return Optional.of(response);
    }

    private static RiwayatSkResponse getLastFromResponseList(List<RiwayatSkResponse> list, EJenisSk jenisSk) {
        return list.stream()
                .filter(sk -> sk.getJenisSk() == jenisSk)
                .findFirst()
                .orElse(null);
    }

    private static PegawaiResponse mapPegawaiResponse(Record record) {
        PegawaiResponse response = new PegawaiResponse();
        response.setId(record.get(PEGAWAI.ID));
        response.setNipam(record.get(PEGAWAI.NIPAM));

        Byte statusPegawaiByte = record.get(PEGAWAI.STATUS_PEGAWAI);
        response.setStatusPegawai(statusPegawaiByte != null ? EStatusPegawai.values()[statusPegawaiByte] : null);

        Byte statusKerjaByte = record.get(PEGAWAI.STATUS_KERJA);
        response.setStatusKerja(statusKerjaByte != null ? EStatusKerja.values()[statusKerjaByte] : null);

        response.setRefSkCapegId(record.get(PEGAWAI.REF_SK_CAPEG_ID));
        response.setTmtKerja(record.get(PEGAWAI.TMT_KERJA));
        response.setTmtPensiun(record.get(PEGAWAI.TMT_PENSIUN));
        response.setRefSkPegawaiId(record.get(PEGAWAI.REF_SK_PEGAWAI_ID));
        response.setTmtPegawai(record.get(PEGAWAI.TMT_PEGAWAI));
        response.setRefSkGolId(record.get(PEGAWAI.REF_SK_GOL_ID));
        response.setTmtGolongan(record.get(PEGAWAI.TMT_GOLONGAN));
        response.setRefSkJabatanId(record.get(PEGAWAI.REF_SK_JABATAN_ID));
        response.setTmtJabatan(record.get(PEGAWAI.TMT_JABATAN));
        response.setRefSkMutasiId(record.get(PEGAWAI.REF_SK_MUTASI_ID));
        response.setTmtMutasi(record.get(PEGAWAI.TMT_MUTASI));
        response.setGajiPokok(record.get(PEGAWAI.GAJI_POKOK));
        response.setPhdp(record.get(PEGAWAI.PHDP));
        response.setJmlTanggungan(record.get(PEGAWAI.JML_TANGGUNGAN));
        response.setIsAskes(record.get(PEGAWAI.IS_ASKES));
        response.setMkgTahun(record.get(PEGAWAI.MKG_TAHUN));
        response.setMkgBulan(record.get(PEGAWAI.MKG_BULAN));
        response.setEmail(record.get(PEGAWAI.EMAIL));
        response.setAbsensiId(record.get(PEGAWAI.ABSENSI_ID));
        response.setNotes(record.get(PEGAWAI.NOTES));

        String bioNik = record.get("biodata_nik", String.class);
        if (bioNik != null) {
            response.setBiodata(new PegawaiResponse.Biodata(
                    bioNik,
                    record.get("biodata_nama", String.class),
                    record.get("biodata_gelar_depan", String.class),
                    record.get("biodata_gelar_belakang", String.class)
            ));
        }

        Long orgId = record.get("organisasi_id", Long.class);
        if (orgId != null) {
            response.setOrganisasi(new PegawaiResponse.Organisasi(
                    orgId,
                    record.get("organisasi_nama", String.class)
            ));
        }

        Long jabId = record.get("jabatan_id", Long.class);
        if (jabId != null) {
            response.setJabatan(new PegawaiResponse.Jabatan(
                    jabId,
                    record.get("jabatan_nama", String.class)
            ));
        }

        Long profId = record.get("profesi_id", Long.class);
        if (profId != null) {
            response.setProfesi(new PegawaiResponse.Profesi(
                    profId,
                    record.get("profesi_nama", String.class)
            ));
        }

        Long golId = record.get("golongan_id", Long.class);
        if (golId != null) {
            response.setGolongan(new PegawaiResponse.Golongan(
                    golId,
                    record.get("golongan_golongan", String.class),
                    record.get("golongan_pangkat", String.class)
            ));
        }

        Long grdId = record.get("grade_id", Long.class);
        if (grdId != null) {
            response.setGrade(new PegawaiResponse.Grade(
                    grdId,
                    record.get("grade_grade", Integer.class)
            ));
        }

        Long pajakId = record.get("kode_pajak_id", Long.class);
        if (pajakId != null) {
            String pajakKode = record.get("kode_pajak_kode", String.class);
            response.setKodePajak(new PegawaiResponse.KodePajak(
                    pajakId,
                    pajakKode,
                    pajakKode
            ));
        }

        return response;
    }

    private static PegawaiListResponse mapPegawaiListResponse(Record record) {
        PegawaiListResponse response = new PegawaiListResponse();
        response.setId(record.get(PEGAWAI.ID));
        response.setNipam(record.get(PEGAWAI.NIPAM));
        response.setNama(record.get("biodata_nama", String.class));

        Byte statusPegawaiByte = record.get(PEGAWAI.STATUS_PEGAWAI);
        response.setStatusPegawai(statusPegawaiByte != null ? EStatusPegawai.values()[statusPegawaiByte] : null);

        Long orgId = record.get("organisasi_id", Long.class);
        if (orgId != null) {
            OrganisasiMiniResponse org = new OrganisasiMiniResponse();
            org.setId(orgId);
            org.setKode(record.get("organisasi_kode", String.class));
            org.setNama(record.get("organisasi_nama", String.class));
            org.setShortName(record.get("organisasi_short_name", String.class));
            response.setOrganisasi(org);
        }

        Long jabId = record.get("jabatan_id", Long.class);
        if (jabId != null) {
            JabatanMiniResponse jab = new JabatanMiniResponse();
            jab.setId(jabId);
            jab.setKode(record.get("jabatan_kode", String.class));
            jab.setNama(record.get("jabatan_nama", String.class));

            Long lvlId = record.get("level_id", Long.class);
            if (lvlId != null) {
                jab.setLevel(new LevelResponse(lvlId, record.get("level_nama", String.class)));
            }
            response.setJabatan(jab);
        }

        Long golId = record.get("golongan_id", Long.class);
        if (golId != null) {
            response.setGolongan(new GolonganResponse(
                    golId,
                    record.get("golongan_golongan", String.class),
                    record.get("golongan_pangkat", String.class)
            ));
        }

        return response;
    }

    private List<RiwayatSkResponse> findRiwayatSkList(Long pegawaiId) {
        return dsl.select(
                        RIWAYAT_SK.ID,
                        RIWAYAT_SK.NIPAM,
                        RIWAYAT_SK.NAMA,
                        RIWAYAT_SK.NOMOR_SK,
                        RIWAYAT_SK.JENIS_SK,
                        RIWAYAT_SK.TANGGAL_SK,
                        RIWAYAT_SK.TMT_BERLAKU,
                        RIWAYAT_SK.GAJI_POKOK,
                        RIWAYAT_SK.MKG_TAHUN,
                        RIWAYAT_SK.MKG_BULAN,
                        RIWAYAT_SK.KENAIKAN_BERIKUTNYA,
                        RIWAYAT_SK.MKGB_TAHUN,
                        RIWAYAT_SK.MKGB_BULAN,
                        RIWAYAT_SK.UPDATE_MASTER,
                        RIWAYAT_SK.NOTES,
                        GOLONGAN.ID.as("golongan_id"),
                        GOLONGAN.GOLONGAN_.as("golongan_golongan"),
                        GOLONGAN.PANGKAT.as("golongan_pangkat")
                ).from(RIWAYAT_SK)
                .leftJoin(GOLONGAN).on(RIWAYAT_SK.GOLONGAN_ID.eq(GOLONGAN.ID))
                .where(RIWAYAT_SK.PEGAWAI_ID.eq(pegawaiId).and(RIWAYAT_SK.IS_DELETED.eq(false)))
                .orderBy(RIWAYAT_SK.TMT_BERLAKU.desc())
                .fetch(r -> {
                    RiwayatSkResponse res = new RiwayatSkResponse();
                    res.setId(r.get(RIWAYAT_SK.ID));
                    res.setNipam(r.get(RIWAYAT_SK.NIPAM));
                    res.setNama(r.get(RIWAYAT_SK.NAMA));
                    res.setNomorSk(r.get(RIWAYAT_SK.NOMOR_SK));

                    Byte jsByte = r.get(RIWAYAT_SK.JENIS_SK);
                    res.setJenisSk(jsByte != null ? EJenisSk.values()[jsByte] : null);

                    res.setTanggalSk(r.get(RIWAYAT_SK.TANGGAL_SK));
                    res.setTmtBerlaku(r.get(RIWAYAT_SK.TMT_BERLAKU));

                    Long golId = r.get("golongan_id", Long.class);
                    if (golId != null) {
                        res.setGolongan(new GolonganResponse(
                                golId,
                                r.get("golongan_golongan", String.class),
                                r.get("golongan_pangkat", String.class)
                        ));
                    }

                    res.setGajiPokok(r.get(RIWAYAT_SK.GAJI_POKOK));
                    res.setMkgTahun(r.get(RIWAYAT_SK.MKG_TAHUN));
                    res.setMkgBulan(r.get(RIWAYAT_SK.MKG_BULAN));
                    res.setKenaikanBerikutnya(r.get(RIWAYAT_SK.KENAIKAN_BERIKUTNYA));
                    res.setMkgbTahun(r.get(RIWAYAT_SK.MKGB_TAHUN));
                    res.setMkgbBulan(r.get(RIWAYAT_SK.MKGB_BULAN));
                    res.setUpdateMaster(r.get(RIWAYAT_SK.UPDATE_MASTER));
                    res.setNotes(r.get(RIWAYAT_SK.NOTES));
                    return res;
                });
    }
}
