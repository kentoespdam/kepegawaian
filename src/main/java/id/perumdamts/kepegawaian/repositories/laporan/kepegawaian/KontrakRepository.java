package id.perumdamts.kepegawaian.repositories.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EFilterKontrak;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.KontrakResponse;
import id.perumdamts.kepegawaian.mapper.laporan.kepegawaian.KontrakRecordMapper;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;
import static id.perumdamts.kepegawaian.jooq.tables.RiwayatKontrak.RIWAYAT_KONTRAK;

@Repository
@RequiredArgsConstructor
public class KontrakRepository {
    private final DSLContext dsl;

    public List<KontrakResponse> fetch(EFilterKontrak filter) {
        var sisaTahun = DSL.field("TIMESTAMPDIFF(YEAR, NOW(), {0})", Integer.class, RIWAYAT_KONTRAK.TANGGAL_SELESAI);
        var sisaBulan = DSL.field("TIMESTAMPDIFF(MONTH, NOW(), {0})", Integer.class, RIWAYAT_KONTRAK.TANGGAL_SELESAI);

        Condition condition = RIWAYAT_KONTRAK.NOMOR_KONTRAK.isNotNull()
                .and(PEGAWAI.STATUS_PEGAWAI.eq((byte) EStatusPegawai.KONTRAK.ordinal()));

        condition = applyFilter(condition, filter);

        return dsl.select(
                        PEGAWAI.NIPAM, BIODATA.NAMA, RIWAYAT_KONTRAK.NOMOR_KONTRAK,
                        ORGANISASI.NAMA.as("nama_organisasi"), JABATAN.NAMA.as("nama_jabatan"),
                        RIWAYAT_KONTRAK.TANGGAL_MULAI, RIWAYAT_KONTRAK.TANGGAL_SELESAI,
                        sisaTahun.as("sisa_tahun"), sisaBulan.as("sisa_bulan")
                )
                .from(PEGAWAI)
                .join(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(RIWAYAT_KONTRAK).on(PEGAWAI.ID.eq(RIWAYAT_KONTRAK.PEGAWAI_ID)
                        .and(RIWAYAT_KONTRAK.IS_LATEST.eq(true)))
                .join(ORGANISASI).on(PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .join(JABATAN).on(PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .where(condition)
                .fetch(KontrakRecordMapper::map);
    }

    private Condition applyFilter(Condition base, EFilterKontrak filter) {
        return switch (filter) {
            case AKTIF -> base.and(PEGAWAI.STATUS_KERJA.eq((byte) EStatusKerja.KARYAWAN_AKTIF.ordinal()))
                    .and(RIWAYAT_KONTRAK.TANGGAL_SELESAI.ge(LocalDate.now()));
            case THIS_MONTH -> base.and(PEGAWAI.STATUS_KERJA.eq((byte) EStatusKerja.KARYAWAN_AKTIF.ordinal()))
                    .and(DSL.field("YEAR({0})", Integer.class, RIWAYAT_KONTRAK.TANGGAL_SELESAI).eq(DSL.field("YEAR(CURDATE())", Integer.class)))
                    .and(DSL.field("MONTH({0})", Integer.class, RIWAYAT_KONTRAK.TANGGAL_SELESAI).eq(DSL.field("MONTH(CURDATE())", Integer.class)));
            case GTE_1_MONTH -> base.and(PEGAWAI.STATUS_KERJA.eq((byte) EStatusKerja.KARYAWAN_AKTIF.ordinal()))
                    .and(DSL.field("YEAR({0})", Integer.class, RIWAYAT_KONTRAK.TANGGAL_SELESAI).eq(DSL.field("YEAR(CURDATE())", Integer.class)))
                    .and(DSL.field("MONTH({0})", Integer.class, RIWAYAT_KONTRAK.TANGGAL_SELESAI).eq(DSL.field("MONTH(CURDATE())+1", Integer.class)));
            case GTE_2_MONTH -> base.and(PEGAWAI.STATUS_KERJA.eq((byte) EStatusKerja.KARYAWAN_AKTIF.ordinal()))
                    .and(DSL.field("YEAR({0})", Integer.class, RIWAYAT_KONTRAK.TANGGAL_SELESAI).eq(DSL.field("YEAR(CURDATE())", Integer.class)))
                    .and(DSL.field("MONTH({0})", Integer.class, RIWAYAT_KONTRAK.TANGGAL_SELESAI).eq(DSL.field("MONTH(CURDATE())+2", Integer.class)));
            case GTE_3_MONTH -> base.and(PEGAWAI.STATUS_KERJA.eq((byte) EStatusKerja.KARYAWAN_AKTIF.ordinal()))
                    .and(DSL.field("YEAR({0})", Integer.class, RIWAYAT_KONTRAK.TANGGAL_SELESAI).eq(DSL.field("YEAR(CURDATE())", Integer.class)))
                    .and(DSL.field("MONTH({0})", Integer.class, RIWAYAT_KONTRAK.TANGGAL_SELESAI).eq(DSL.field("MONTH(CURDATE())+3", Integer.class)));
            case ENDED -> base.and(PEGAWAI.STATUS_KERJA.in(
                    (byte) EStatusKerja.DIRUMAHKAN.ordinal(),
                    (byte) EStatusKerja.BERHENTI_OR_KELUAR.ordinal()));
        };
    }
}
