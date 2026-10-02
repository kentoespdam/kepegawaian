package id.perumdamts.kepegawaian.repositories.kepegawaian.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiRequest;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiResponse;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.mapper.pegawai.pegawai.PegawaiRecordMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SelectField;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.GajiPendapatanNonPajak.GAJI_PENDAPATAN_NON_PAJAK;
import static id.perumdamts.kepegawaian.jooq.tables.Golongan.GOLONGAN;
import static id.perumdamts.kepegawaian.jooq.tables.Grade.GRADE;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;
import static id.perumdamts.kepegawaian.jooq.tables.Pendidikan.PENDIDIKAN;
import static id.perumdamts.kepegawaian.jooq.tables.Profesi.PROFESI;

@Repository
@RequiredArgsConstructor
public class CalonPensiunQueryRepository {
    private final DSLContext dsl;

    private static final Map<String, Field<?>> ALLOWED_SORTS = Map.ofEntries(
            Map.entry("id", PEGAWAI.ID),
            Map.entry("nipam", PEGAWAI.NIPAM),
            Map.entry("nama", BIODATA.NAMA),
            Map.entry("biodata.nama", BIODATA.NAMA),
            Map.entry("Biodata.nama", BIODATA.NAMA),
            Map.entry("tmtPensiun", PEGAWAI.TMT_PENSIUN),
            Map.entry("jabatanId", JABATAN.NAMA),
            Map.entry("organisasiId", ORGANISASI.NAMA),
            Map.entry("golonganId", GOLONGAN.GOLONGAN_)
    );

    public Page<PegawaiResponse> findPage(RiwayatTerminasiRequest request) {
        Condition condition = buildConditions(request);
        var sortOrder = SortParam.resolve(request.getSortBy(), request.getSortDirection(), ALLOWED_SORTS, BIODATA.NAMA);

        var total = dsl.selectCount()
                .from(PEGAWAI)
                .leftJoin(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .where(condition)
                .fetchOptional(0, Long.class).orElse(0L);

        var rows = dsl.select(selectFields())
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
                .where(condition)
                .orderBy(sortOrder)
                .limit(request.getSizeOrDefault())
                .offset(request.offset())
                .fetch(PegawaiRecordMapper::mapResponse);

        return new PageImpl<>(rows, PageRequest.of(request.getPageNumber(), request.getSizeOrDefault()), total);
    }

    private Condition buildConditions(RiwayatTerminasiRequest request) {
        var condition = DSL.trueCondition().and(PEGAWAI.IS_DELETED.eq(false));
        condition = condition.and(PEGAWAI.STATUS_KERJA.eq((byte) EStatusKerja.KARYAWAN_AKTIF.ordinal()));

        if (request.getPegawaiId() != null) {
            condition = condition.and(PEGAWAI.ID.eq(request.getPegawaiId()));
        }
        if (request.getNipam() != null && !request.getNipam().isBlank()) {
            condition = condition.and(PEGAWAI.NIPAM.eq(request.getNipam()));
        }
        if (request.getNama() != null && !request.getNama().isBlank()) {
            condition = condition.and(BIODATA.NAMA.containsIgnoreCase(request.getNama()));
        }
        if (request.getJabatanId() != null) {
            condition = condition.and(PEGAWAI.JABATAN_ID.eq(request.getJabatanId()));
        }
        if (request.getGolonganId() != null) {
            condition = condition.and(PEGAWAI.GOLONGAN_ID.eq(request.getGolonganId()));
        }
        if (request.getOrganisasiId() != null) {
            condition = condition.and(PEGAWAI.ORGANISASI_ID.eq(request.getOrganisasiId()));
        }
        if (request.getTanggalTerminasi() != null) {
            condition = condition.and(PEGAWAI.TMT_PENSIUN.le(request.getTanggalTerminasi()));
        }
        if (request.getTahunPensiun() != null) {
            condition = condition.and(PEGAWAI.TMT_PENSIUN.between(
                    LocalDate.of(request.getTahunPensiun(), 1, 1),
                    LocalDate.of(request.getTahunPensiun(), 12, 31)
            ));
        }

        return condition;
    }

    private SelectField<?>[] selectFields() {
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
}
