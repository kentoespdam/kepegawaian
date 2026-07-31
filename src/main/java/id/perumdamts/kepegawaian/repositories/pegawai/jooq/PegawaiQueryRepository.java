package id.perumdamts.kepegawaian.repositories.pegawai.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkResponse;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiListRequest;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiListResponse;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiRequest;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiResponse;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiResponseDetail;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiTableResponse;
import id.perumdamts.kepegawaian.mapper.kepegawaian.RiwayatSkJooqMapper;
import id.perumdamts.kepegawaian.mapper.pegawai.pegawai.PegawaiDetailRecordMapper;
import id.perumdamts.kepegawaian.mapper.pegawai.pegawai.PegawaiRecordMapper;
import id.perumdamts.kepegawaian.mapper.pegawai.pegawai.PegawaiTableRecordMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SelectField;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

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

    private SelectField<?>[] pegawaiResponseFields() {
        return new SelectField<?>[]{
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
        };
    }

    private SelectField<?>[] pegawaiTableFields() {
        return new SelectField<?>[]{
                PEGAWAI.ID,
                PEGAWAI.NIPAM,
                PEGAWAI.STATUS_PEGAWAI,
                PEGAWAI.TMT_PENSIUN,
                PEGAWAI.IS_ASKES,
                BIODATA.NAMA.as("biodata_nama"),
                BIODATA.JENIS_KELAMIN.as("biodata_jenis_kelamin"),
                BIODATA.TANGGAL_LAHIR.as("biodata_tanggal_lahir"),
                BIODATA.STATUS_KAWIN.as("biodata_status_kawin"),
                ORGANISASI.ID.as("organisasi_id"),
                ORGANISASI.NAMA.as("organisasi_nama"),
                JABATAN.ID.as("jabatan_id"),
                JABATAN.NAMA.as("jabatan_nama"),
                PROFESI.ID.as("profesi_id"),
                PROFESI.NAMA.as("profesi_nama"),
                GOLONGAN.PANGKAT.as("golongan_pangkat"),
                GOLONGAN.GOLONGAN_.as("golongan_golongan"),
                GAJI_PENDAPATAN_NON_PAJAK.KODE.as("kode_pajak")
        };
    }

    private SelectField<?>[] pegawaiListResponseFields() {
        return new SelectField<?>[]{
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
        };
    }

    public Page<PegawaiTableResponse> findTablePage(PegawaiRequest request) {
        var conditions = buildConditions(request);
        var sortOrder = SortParam.resolve(request.getSortBy(), request.getSortDirection(), ALLOWED_SORTS, PEGAWAI.ID);

        var total = dsl.selectCount()
                .from(PEGAWAI)
                .leftJoin(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .where(conditions)
                .fetchOneInto(Long.class);

        var rows = dsl.select(pegawaiTableFields())
                .from(PEGAWAI)
                .leftJoin(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(ORGANISASI).on(PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(PROFESI).on(PEGAWAI.PROFESI_ID.eq(PROFESI.ID))
                .leftJoin(GOLONGAN).on(PEGAWAI.GOLONGAN_ID.eq(GOLONGAN.ID))
                .leftJoin(GAJI_PENDAPATAN_NON_PAJAK).on(PEGAWAI.GAJI_PENDAPATAN_NON_PAJAK_ID.eq(GAJI_PENDAPATAN_NON_PAJAK.ID))
                .where(conditions)
                .orderBy(sortOrder)
                .limit(request.getSizeOrDefault())
                .offset(request.offset())
                .fetch(PegawaiTableRecordMapper::mapTableResponse);

        return new PageImpl<>(rows, PageRequest.of(request.getPageNumber(), request.getSizeOrDefault()), total != null ? total : 0L);
    }

    public List<PegawaiListResponse> findAll(PegawaiRequest request) {
        var conditions = buildConditions(request);
        var sortOrder = SortParam.resolve(request.getSortBy(), request.getSortDirection(), ALLOWED_SORTS, PEGAWAI.ID);

        return dsl.select(pegawaiListResponseFields())
                .from(PEGAWAI)
                .leftJoin(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(ORGANISASI).on(PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(LEVEL).on(JABATAN.LEVEL_ID.eq(LEVEL.ID))
                .leftJoin(GOLONGAN).on(PEGAWAI.GOLONGAN_ID.eq(GOLONGAN.ID))
                .where(conditions)
                .orderBy(sortOrder)
                .fetch(PegawaiRecordMapper::mapListResponse);
    }

    public List<PegawaiListResponse> findAll(PegawaiListRequest request) {
        var conditions = DSL.trueCondition().and(PEGAWAI.IS_DELETED.eq(false));

        if (request.getSearch() != null && !request.getSearch().isBlank()) {
            conditions = conditions.and(
                    PEGAWAI.NIPAM.containsIgnoreCase(request.getSearch())
                            .or(BIODATA.NAMA.containsIgnoreCase(request.getSearch()))
            );
        }
        if (request.getStatusKerja() != null) {
            conditions = conditions.and(PEGAWAI.STATUS_KERJA.eq((byte) request.getStatusKerja().ordinal()));
        }

        return dsl.select(pegawaiListResponseFields())
                .from(PEGAWAI)
                .leftJoin(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(ORGANISASI).on(PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(LEVEL).on(JABATAN.LEVEL_ID.eq(LEVEL.ID))
                .leftJoin(GOLONGAN).on(PEGAWAI.GOLONGAN_ID.eq(GOLONGAN.ID))
                .where(conditions)
                .orderBy(BIODATA.NAMA.asc())
                .fetch(PegawaiRecordMapper::mapListResponse);
    }

    public List<PegawaiListResponse> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return dsl.select(pegawaiListResponseFields())
                .from(PEGAWAI)
                .leftJoin(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(ORGANISASI).on(PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(LEVEL).on(JABATAN.LEVEL_ID.eq(LEVEL.ID))
                .leftJoin(GOLONGAN).on(PEGAWAI.GOLONGAN_ID.eq(GOLONGAN.ID))
                .where(PEGAWAI.ID.in(ids).and(PEGAWAI.IS_DELETED.eq(false)))
                .fetch(PegawaiRecordMapper::mapListResponse);
    }

    public Optional<PegawaiResponse> findByNipam(String nipam) {
        if (nipam == null || nipam.isBlank()) {
            return Optional.empty();
        }
        return dsl.select(pegawaiResponseFields())
                .from(PEGAWAI)
                .leftJoin(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
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
                .fetchOptional(PegawaiRecordMapper::mapResponse);
    }

    public Optional<PegawaiResponseDetail> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        var kartuIdentitasMultiset = multiset(
                select(PegawaiDetailSelects.kartuIdentitasFields())
                        .from(KARTU_IDENTITAS)
                        .leftJoin(JENIS_KITAS).on(KARTU_IDENTITAS.JENIS_KITAS_ID.eq(JENIS_KITAS.ID))
                        .where(KARTU_IDENTITAS.NIK.eq(BIODATA.NIK))
                        .and(KARTU_IDENTITAS.IS_DELETED.eq(false))
        ).as("kartu_identitas");

        return dsl.select(PegawaiDetailSelects.detailFields())
                .select(kartuIdentitasMultiset)
                .from(PEGAWAI)
                .leftJoin(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
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
                .fetchOptional(record -> {
                    List<RiwayatSkResponse> skList = findRiwayatSkList(id);
                    return PegawaiDetailRecordMapper.mapDetail(record, skList);
                });
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
                .fetch(RiwayatSkJooqMapper::mapRiwayatSk);
    }
}
