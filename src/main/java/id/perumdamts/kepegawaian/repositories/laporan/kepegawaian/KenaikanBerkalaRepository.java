package id.perumdamts.kepegawaian.repositories.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EFilterKenaikanBerkala;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EJenisKenaikanBerkala;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.KenaikanBerkalaResponse;
import id.perumdamts.kepegawaian.mapper.laporan.kepegawaian.KenaikanBerkalaRecordMapper;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.Golongan.GOLONGAN;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.JenjangPendidikan.JENJANG_PENDIDIKAN;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;
import static id.perumdamts.kepegawaian.jooq.tables.Pendidikan.PENDIDIKAN;
import static id.perumdamts.kepegawaian.jooq.tables.RiwayatSk.RIWAYAT_SK;
import static id.perumdamts.kepegawaian.jooq.tables.RiwayatSp.RIWAYAT_SP;
import static id.perumdamts.kepegawaian.jooq.tables.SanksiSp.SANKSI_SP;

@Repository
@RequiredArgsConstructor
public class KenaikanBerkalaRepository {
    private final DSLContext dsl;

    public List<KenaikanBerkalaResponse> fetch(EFilterKenaikanBerkala filter, EJenisKenaikanBerkala jenisSk) {
        var baseCondition = getBaseCondition(jenisSk);
        var timeCondition = getTimeCondition(filter);

        var mkgTahun = DSL.field("TIMESTAMPDIFF(YEAR, {0}, CURDATE())", Integer.class, PEGAWAI.TMT_GOLONGAN);
        var mkgBulan = DSL.field("TIMESTAMPDIFF(MONTH, {0}, CURDATE())", Integer.class, PEGAWAI.TMT_GOLONGAN);
        var mkTahun = DSL.field("TIMESTAMPDIFF(YEAR, {0}, CURDATE())", Integer.class, PEGAWAI.TMT_KERJA);
        var mkBulan = DSL.field("TIMESTAMPDIFF(MONTH, {0}, CURDATE())", Integer.class, PEGAWAI.TMT_KERJA);
        var pendidikan = DSL.field("CONCAT_WS('-', {0}, {1})", String.class, JENJANG_PENDIDIKAN.NAMA, PENDIDIKAN.JURUSAN);

        return dsl.select(
                        RIWAYAT_SK.ID, PEGAWAI.ID.as("pegawai_id"),
                        PEGAWAI.NIPAM, BIODATA.NAMA,
                        RIWAYAT_SK.JENIS_SK, RIWAYAT_SK.NOMOR_SK,
                        RIWAYAT_SK.TMT_BERLAKU, RIWAYAT_SK.KENAIKAN_BERIKUTNYA,
                        RIWAYAT_SP.TANGGAL_EKSEKUSI_SANKSI,
                        SANKSI_SP.IS_PENDING_GAJI, SANKSI_SP.IS_PENDING_PANGKAT,
                        JABATAN.NAMA.as("nama_jabatan"), PEGAWAI.TMT_JABATAN,
                        GOLONGAN.GOLONGAN_, GOLONGAN.PANGKAT, PEGAWAI.TMT_GOLONGAN,
                        mkgTahun.as("mkg_tahun"), mkgBulan.as("mkg_bulan"),
                        PEGAWAI.TMT_KERJA,
                        mkTahun.as("mk_tahun"), mkBulan.as("mk_bulan"),
                        pendidikan.as("pendidikan_terakhir"),
                        BIODATA.TEMPAT_LAHIR, BIODATA.TANGGAL_LAHIR
                )
                .from(RIWAYAT_SK)
                .join(PEGAWAI).on(RIWAYAT_SK.PEGAWAI_ID.eq(PEGAWAI.ID)
                        .and(PEGAWAI.IS_DELETED.eq(false))
                        .and(PEGAWAI.STATUS_KERJA.in(
                                (byte) EStatusKerja.DIRUMAHKAN.ordinal(),
                                (byte) EStatusKerja.KARYAWAN_AKTIF.ordinal()))
                        .and(PEGAWAI.STATUS_PEGAWAI.eq((byte) EStatusPegawai.PEGAWAI.ordinal())))
                .join(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .join(GOLONGAN).on(PEGAWAI.GOLONGAN_ID.eq(GOLONGAN.ID))
                .join(JABATAN).on(PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .join(PENDIDIKAN).on(BIODATA.NIK.eq(PENDIDIKAN.BIODATA_ID).and(PENDIDIKAN.IS_LATEST.eq((byte) 1)))
                .join(JENJANG_PENDIDIKAN).on(PENDIDIKAN.JENJANG_ID.eq(JENJANG_PENDIDIKAN.ID))
                .leftJoin(RIWAYAT_SP).on(PEGAWAI.ID.eq(RIWAYAT_SP.PEGAWAI_ID))
                .leftJoin(SANKSI_SP).on(RIWAYAT_SP.SANKSI_ID.eq(SANKSI_SP.ID))
                .where(baseCondition.and(timeCondition))
                .fetch(KenaikanBerkalaRecordMapper::map);
    }

    public Long count(EFilterKenaikanBerkala filter, EJenisKenaikanBerkala jenisSk) {
        var baseCondition = getBaseCondition(jenisSk);
        var timeCondition = getTimeCondition(filter);

        return dsl.selectCount()
                .from(RIWAYAT_SK)
                .join(PEGAWAI).on(RIWAYAT_SK.PEGAWAI_ID.eq(PEGAWAI.ID)
                        .and(PEGAWAI.IS_DELETED.eq(false))
                        .and(PEGAWAI.STATUS_KERJA.in(
                                (byte) EStatusKerja.DIRUMAHKAN.ordinal(),
                                (byte) EStatusKerja.KARYAWAN_AKTIF.ordinal()))
                        .and(PEGAWAI.STATUS_PEGAWAI.eq((byte) EStatusPegawai.PEGAWAI.ordinal())))
                .join(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .join(GOLONGAN).on(PEGAWAI.GOLONGAN_ID.eq(GOLONGAN.ID))
                .join(JABATAN).on(PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .join(PENDIDIKAN).on(BIODATA.NIK.eq(PENDIDIKAN.BIODATA_ID).and(PENDIDIKAN.IS_LATEST.eq((byte) 1)))
                .join(JENJANG_PENDIDIKAN).on(PENDIDIKAN.JENJANG_ID.eq(JENJANG_PENDIDIKAN.ID))
                .leftJoin(RIWAYAT_SP).on(PEGAWAI.ID.eq(RIWAYAT_SP.PEGAWAI_ID))
                .leftJoin(SANKSI_SP).on(RIWAYAT_SP.SANKSI_ID.eq(SANKSI_SP.ID))
                .where(baseCondition.and(timeCondition))
                .fetchOptional(0, Long.class).orElse(0L);
    }

    private Condition getBaseCondition(EJenisKenaikanBerkala jenisSk) {
        return RIWAYAT_SK.IS_DELETED.eq(false)
                .and(RIWAYAT_SK.JENIS_SK.eq((byte) jenisSk.ordinal()));
    }

    private Condition getTimeCondition(EFilterKenaikanBerkala filter) {
        var now = LocalDate.now();
        Field<Integer> yearField = DSL.field("YEAR({0})", Integer.class, RIWAYAT_SK.KENAIKAN_BERIKUTNYA);
        Field<Integer> monthField = DSL.field("MONTH({0})", Integer.class, RIWAYAT_SK.KENAIKAN_BERIKUTNYA);

        return switch (filter) {
            case BULAN_INI -> yearField.eq(now.getYear()).and(monthField.eq(now.getMonthValue()));
            case GTE_1 -> {
                var ym = now.plusMonths(1);
                yield yearField.eq(ym.getYear()).and(monthField.eq(ym.getMonthValue()));
            }
            case GTE_2 -> {
                var ym = now.plusMonths(2);
                yield yearField.eq(ym.getYear()).and(monthField.eq(ym.getMonthValue()));
            }
            case TAHUN_INI -> yearField.eq(now.getYear());
        };
    }
}
