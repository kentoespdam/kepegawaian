package id.perumdamts.kepegawaian.repositories.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EFilterLta;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.LtaResponse;
import id.perumdamts.kepegawaian.mapper.laporan.kepegawaian.LtaRecordMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;
import static id.perumdamts.kepegawaian.jooq.tables.ProfilKeluarga.PROFIL_KELUARGA;

@Repository
@RequiredArgsConstructor
public class LtaRepository {
    private final DSLContext dsl;

    public List<LtaResponse> fetch(EFilterLta filter) {
        var ym = resolveTargetMonth(filter);
        var targetDate = DSL.val(ym.atDay(15));
        var umurField = DSL.field("TIMESTAMPDIFF(YEAR, {0}, {1})", Integer.class, PROFIL_KELUARGA.TANGGAL_LAHIR, targetDate);
        var bulanLahir = DSL.field("MONTH({0})", Integer.class, PROFIL_KELUARGA.TANGGAL_LAHIR);

        return dsl.select(
                        PROFIL_KELUARGA.ID,
                        PROFIL_KELUARGA.NAMA,
                        DSL.field("IF({0} = 0, 'Pria', 'Wanita')", String.class, PROFIL_KELUARGA.JENIS_KELAMIN).as("jenis_kelamin"),
                        PROFIL_KELUARGA.TANGGAL_LAHIR,
                        umurField.as("umur"),
                        PROFIL_KELUARGA.TANGGUNGAN,
                        DSL.field("CASE {0} WHEN 0 THEN 'Belum Sekolah' WHEN 1 THEN 'Sekolah' ELSE 'Selesai Sekolah' END",
                                String.class, PROFIL_KELUARGA.STATUS_PENDIDIKAN).as("status_pendidikan"),
                        BIODATA.NAMA.as("nama_karyawan"),
                        PEGAWAI.NIPAM,
                        JABATAN.NAMA.as("nama_jabatan")
                )
                .from(PROFIL_KELUARGA)
                .join(BIODATA).on(PROFIL_KELUARGA.BIODATA_ID.eq(BIODATA.NIK))
                .join(PEGAWAI).on(BIODATA.NIK.eq(PEGAWAI.BIODATA_ID).and(PEGAWAI.IS_DELETED.eq(false)))
                .join(JABATAN).on(PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .where(PEGAWAI.STATUS_KERJA.in((byte) 1, (byte) 2))
                        .and(PROFIL_KELUARGA.HUBUNGAN_KELUARGA.eq((byte) 4))
                        .and(PROFIL_KELUARGA.STATUS_PENDIDIKAN.ne((byte) 2))
                        .and(bulanLahir.eq(ym.getMonthValue()))
                        .and(umurField.ge(21).and(umurField.le(26)))
                .fetch(LtaRecordMapper::map);
    }

    public Long count(EFilterLta filter) {
        var ym = resolveTargetMonth(filter);
        var targetDate = DSL.val(ym.atDay(15));
        var umurField = DSL.field("TIMESTAMPDIFF(YEAR, {0}, {1})", Integer.class, PROFIL_KELUARGA.TANGGAL_LAHIR, targetDate);
        var bulanLahir = DSL.field("MONTH({0})", Integer.class, PROFIL_KELUARGA.TANGGAL_LAHIR);

        return dsl.selectCount()
                .from(PROFIL_KELUARGA)
                .join(BIODATA).on(PROFIL_KELUARGA.BIODATA_ID.eq(BIODATA.NIK))
                .join(PEGAWAI).on(BIODATA.NIK.eq(PEGAWAI.BIODATA_ID).and(PEGAWAI.IS_DELETED.eq(false)))
                .join(JABATAN).on(PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .where(PEGAWAI.STATUS_KERJA.in((byte) 1, (byte) 2))
                        .and(PROFIL_KELUARGA.HUBUNGAN_KELUARGA.eq((byte) 4))
                        .and(PROFIL_KELUARGA.STATUS_PENDIDIKAN.ne((byte) 2))
                        .and(bulanLahir.eq(ym.getMonthValue()))
                        .and(umurField.ge(21).and(umurField.le(26)))
                .fetchOptional(0, Long.class).orElse(0L);
    }

    private YearMonth resolveTargetMonth(EFilterLta filter) {
        var now = LocalDate.now();
        int adjustment = switch (filter) {
            case BULAN_INI -> 0;
            case GTE_1 -> 1;
            case GTE_2 -> 2;
        };
        int totalMonths = now.getMonthValue() + adjustment;
        int targetYear = now.getYear() + (totalMonths - 1) / 12;
        int targetMonth = (totalMonths - 1) % 12 + 1;
        return YearMonth.of(targetYear, targetMonth);
    }
}
