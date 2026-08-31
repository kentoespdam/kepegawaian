package id.perumdamts.kepegawaian.repositories.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.*;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.Golongan.GOLONGAN;
import static id.perumdamts.kepegawaian.jooq.tables.JenjangPendidikan.JENJANG_PENDIDIKAN;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;
import static id.perumdamts.kepegawaian.jooq.tables.Pendidikan.PENDIDIKAN;
import static id.perumdamts.kepegawaian.jooq.tables.StatistikPegawai.STATISTIK_PEGAWAI;

@Repository
@RequiredArgsConstructor
public class StatistikRepository {
    private final DSLContext dsl;

    private static final byte STATUS_KERJA_AKTIF = (byte) EStatusKerja.KARYAWAN_AKTIF.ordinal();

    public List<StatistikGolonganResponse> fetchByGolongan() {
        var totalAll = dsl.selectCount().from(PEGAWAI)
                .where(PEGAWAI.IS_DELETED.eq(false).and(PEGAWAI.STATUS_KERJA.eq(STATUS_KERJA_AKTIF)))
                .fetchOptional(0, Long.class).orElse(1L);

        return dsl.select(
                        DSL.coalesce(GOLONGAN.GOLONGAN_, DSL.val("--")).as("golongan"),
                        DSL.coalesce(GOLONGAN.PANGKAT, DSL.val("--")).as("pangkat"),
                        DSL.sum(DSL.field("IF({0} = 0, 1, 0)", Integer.class, BIODATA.JENIS_KELAMIN)).as("jml_l"),
                        DSL.sum(DSL.field("IF({0} = 1, 1, 0)", Integer.class, BIODATA.JENIS_KELAMIN)).as("jml_p"),
                        DSL.count().as("total")
                )
                .from(PEGAWAI)
                .leftJoin(GOLONGAN).on(PEGAWAI.GOLONGAN_ID.eq(GOLONGAN.ID))
                .join(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .where(PEGAWAI.IS_DELETED.eq(false).and(PEGAWAI.STATUS_KERJA.eq(STATUS_KERJA_AKTIF)))
                .groupBy(GOLONGAN.ID)
                .fetch(r -> {
                    var total = r.get("total", Integer.class);
                    return new StatistikGolonganResponse(
                            r.get("golongan", String.class),
                            r.get("pangkat", String.class),
                            r.get("jml_l", Integer.class),
                            r.get("jml_p", Integer.class),
                            total,
                            Math.round(((double) total / totalAll) * 10000.0) / 100.0
                    );
                });
    }

    public List<StatistikPendidikan1Response> fetchByPendidikan1() {
        var totalAll = dsl.selectCount().from(PEGAWAI)
                .where(PEGAWAI.IS_DELETED.eq(false).and(PEGAWAI.STATUS_KERJA.eq(STATUS_KERJA_AKTIF)))
                .fetchOptional(0, Long.class).orElse(1L);

        return dsl.select(JENJANG_PENDIDIKAN.NAMA, DSL.count().as("total"))
                .from(PEGAWAI)
                .join(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .join(JENJANG_PENDIDIKAN).on(BIODATA.PENDIDIKAN_ID.eq(JENJANG_PENDIDIKAN.ID))
                .where(PEGAWAI.IS_DELETED.eq(false).and(PEGAWAI.STATUS_KERJA.eq(STATUS_KERJA_AKTIF)))
                .groupBy(JENJANG_PENDIDIKAN.ID, JENJANG_PENDIDIKAN.NAMA)
                .orderBy(JENJANG_PENDIDIKAN.ID.desc())
                .fetch(r -> {
                    var total = r.get("total", Integer.class);
                    return new StatistikPendidikan1Response(
                            r.get(JENJANG_PENDIDIKAN.NAMA),
                            total,
                            Math.round(((double) total / totalAll) * 10000.0) / 100.0
                    );
                });
    }

    public List<StatistikPendidikan2Response> fetchByPendidikan2(int tahun, int bulan) {
        return dsl.selectFrom(STATISTIK_PEGAWAI)
                .where(STATISTIK_PEGAWAI.TAHUN.eq(tahun).and(STATISTIK_PEGAWAI.BULAN.eq(bulan)))
                .orderBy(STATISTIK_PEGAWAI.SEQ)
                .fetch(r -> new StatistikPendidikan2Response(
                        r.get(STATISTIK_PEGAWAI.ID),
                        r.get(STATISTIK_PEGAWAI.PENDIDIKAN),
                        r.get(STATISTIK_PEGAWAI.NON_GOLONGAN),
                        r.get(STATISTIK_PEGAWAI.GOLONGAN_A),
                        r.get(STATISTIK_PEGAWAI.GOLONGAN_B),
                        r.get(STATISTIK_PEGAWAI.GOLONGAN_C),
                        r.get(STATISTIK_PEGAWAI.GOLONGAN_D),
                        r.get(STATISTIK_PEGAWAI.NON_GOLONGAN) + r.get(STATISTIK_PEGAWAI.GOLONGAN_A) + r.get(STATISTIK_PEGAWAI.GOLONGAN_B) + r.get(STATISTIK_PEGAWAI.GOLONGAN_C) + r.get(STATISTIK_PEGAWAI.GOLONGAN_D),
                        r.get(STATISTIK_PEGAWAI.KONTRAK),
                        r.get(STATISTIK_PEGAWAI.CAPEG),
                        r.get(STATISTIK_PEGAWAI.HONORER),
                        r.get(STATISTIK_PEGAWAI.TETAP),
                        r.get(STATISTIK_PEGAWAI.KONTRAK) + r.get(STATISTIK_PEGAWAI.CAPEG) + r.get(STATISTIK_PEGAWAI.HONORER) + r.get(STATISTIK_PEGAWAI.TETAP),
                        r.get(STATISTIK_PEGAWAI.ADM),
                        r.get(STATISTIK_PEGAWAI.PELAYANAN),
                        r.get(STATISTIK_PEGAWAI.TEKNIK),
                        r.get(STATISTIK_PEGAWAI.ADM) + r.get(STATISTIK_PEGAWAI.PELAYANAN) + r.get(STATISTIK_PEGAWAI.TEKNIK),
                        r.get(STATISTIK_PEGAWAI.PRIA),
                        r.get(STATISTIK_PEGAWAI.WANITA),
                        r.get(STATISTIK_PEGAWAI.PRIA) + r.get(STATISTIK_PEGAWAI.WANITA)
                ));
    }

    public List<StatistikUmurResponse> fetchByUmur() {
        var totalAll = dsl.selectCount().from(PEGAWAI)
                .where(PEGAWAI.IS_DELETED.eq(false).and(PEGAWAI.STATUS_KERJA.eq(STATUS_KERJA_AKTIF)))
                .fetchOptional(0, Long.class).orElse(1L);

        var umur = DSL.field("TIMESTAMPDIFF(YEAR, {0}, NOW())", Integer.class, BIODATA.TANGGAL_LAHIR);

        return dsl.select(umur.as("umur"), DSL.count().as("total"))
                .from(PEGAWAI)
                .join(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .where(PEGAWAI.IS_DELETED.eq(false).and(PEGAWAI.STATUS_KERJA.eq(STATUS_KERJA_AKTIF)))
                .groupBy(umur)
                .orderBy(umur.desc())
                .fetch(r -> {
                    var total = r.get("total", Integer.class);
                    return new StatistikUmurResponse(
                            r.get("umur", Integer.class),
                            total,
                            Math.round(((double) total / totalAll) * 10000.0) / 100.0
                    );
                });
    }

    public List<StatistikJenisKelaminResponse> fetchByJenisKelamin() {
        var totalAll = dsl.selectCount().from(PEGAWAI)
                .where(PEGAWAI.IS_DELETED.eq(false).and(PEGAWAI.STATUS_KERJA.eq(STATUS_KERJA_AKTIF)))
                .fetchOptional(0, Long.class).orElse(1L);

        return dsl.select(
                        DSL.field("IF({0} = 0, 'Laki-laki', 'Perempuan')", String.class, BIODATA.JENIS_KELAMIN).as("jenis_kelamin"),
                        DSL.count().as("total")
                )
                .from(PEGAWAI)
                .join(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .where(PEGAWAI.IS_DELETED.eq(false).and(PEGAWAI.STATUS_KERJA.eq(STATUS_KERJA_AKTIF)))
                .groupBy(BIODATA.JENIS_KELAMIN)
                .fetch(r -> {
                    var total = r.get("total", Integer.class);
                    return new StatistikJenisKelaminResponse(
                            r.get("jenis_kelamin", String.class),
                            total,
                            Math.round(((double) total / totalAll) * 10000.0) / 100.0
                    );
                });
    }

    public List<StatistikGelarResponse> fetchByGelar() {
        var totalAll = dsl.selectCount().from(PEGAWAI)
                .where(PEGAWAI.IS_DELETED.eq(false).and(PEGAWAI.STATUS_KERJA.eq(STATUS_KERJA_AKTIF)))
                .fetchOptional(0, Long.class).orElse(1L);

        return dsl.select(
                        JENJANG_PENDIDIKAN.NAMA.as("jenjang"),
                        DSL.coalesce(PENDIDIKAN.GELAR_BELAKANG, DSL.val("--")).as("gelar"),
                        DSL.count().as("total")
                )
                .from(PEGAWAI)
                .join(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .join(PENDIDIKAN).on(BIODATA.NIK.eq(PENDIDIKAN.BIODATA_ID))
                .join(JENJANG_PENDIDIKAN).on(PENDIDIKAN.JENJANG_ID.eq(JENJANG_PENDIDIKAN.ID))
                        .and(BIODATA.PENDIDIKAN_ID.eq(JENJANG_PENDIDIKAN.ID))
                .where(PEGAWAI.IS_DELETED.eq(false)
                        .and(PEGAWAI.STATUS_KERJA.eq(STATUS_KERJA_AKTIF))
                        .and(PENDIDIKAN.IS_LATEST.eq((byte) 1)))
                .groupBy(PENDIDIKAN.GELAR_BELAKANG, JENJANG_PENDIDIKAN.ID)
                .orderBy(JENJANG_PENDIDIKAN.ID.desc())
                .fetch(r -> {
                    var total = r.get("total", Integer.class);
                    return new StatistikGelarResponse(
                            r.get("jenjang", String.class),
                            r.get("gelar", String.class),
                            total,
                            Math.round(((double) total / totalAll) * 10000.0) / 100.0
                    );
                });
    }

    public List<StatistikAgamaResponse> fetchByAgama() {
        var totalAll = dsl.selectCount().from(PEGAWAI)
                .where(PEGAWAI.IS_DELETED.eq(false).and(PEGAWAI.STATUS_KERJA.eq(STATUS_KERJA_AKTIF)))
                .fetchOptional(0, Long.class).orElse(1L);

        return dsl.select(BIODATA.AGAMA, DSL.count().as("total"))
                .from(PEGAWAI)
                .join(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .where(PEGAWAI.IS_DELETED.eq(false).and(PEGAWAI.STATUS_KERJA.eq(STATUS_KERJA_AKTIF)))
                .groupBy(BIODATA.AGAMA)
                .orderBy(BIODATA.AGAMA)
                .fetch(r -> {
                    var total = r.get("total", Integer.class);
                    Byte agamaByte = r.get(BIODATA.AGAMA);
                    String agamaName = decodeAgama(agamaByte);
                    return new StatistikAgamaResponse(
                            agamaName, total,
                            Math.round(((double) total / totalAll) * 10000.0) / 100.0
                    );
                });
    }

    public List<StatistikStatusPegawaiResponse> fetchByStatusPegawai() {
        var totalAll = dsl.selectCount().from(PEGAWAI)
                .where(PEGAWAI.IS_DELETED.eq(false).and(PEGAWAI.STATUS_KERJA.eq(STATUS_KERJA_AKTIF)))
                .fetchOptional(0, Long.class).orElse(1L);

        return dsl.select(PEGAWAI.STATUS_PEGAWAI, DSL.count().as("total"))
                .from(PEGAWAI)
                .join(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .where(PEGAWAI.IS_DELETED.eq(false).and(PEGAWAI.STATUS_KERJA.eq(STATUS_KERJA_AKTIF)))
                .groupBy(PEGAWAI.STATUS_PEGAWAI)
                .orderBy(PEGAWAI.STATUS_PEGAWAI.desc())
                .fetch(r -> {
                    var total = r.get("total", Integer.class);
                    String statusName = decodeStatusPegawai(r.get(PEGAWAI.STATUS_PEGAWAI));
                    return new StatistikStatusPegawaiResponse(
                            statusName, total,
                            Math.round(((double) total / totalAll) * 10000.0) / 100.0
                    );
                });
    }

    private String decodeAgama(Byte b) {
        if (b == null) return "Invalid";
        return switch (b) {
            case 0 -> "Tidak Tahu";
            case 1 -> "Islam";
            case 2 -> "Kristen";
            case 3 -> "Katolik";
            case 4 -> "Hindu";
            case 5 -> "Budha";
            case 6 -> "Konghuchu";
            case 7 -> "Aliran Kepercayaan";
            case 8 -> "Lainnya";
            default -> "Invalid";
        };
    }

    private String decodeStatusPegawai(Byte b) {
        if (b == null) return "Invalid";
        return switch (b) {
            case 0 -> "Pegawai Kontrak";
            case 1 -> "Calon Pegawai";
            case 2 -> "Pegawai Tetap";
            case 3 -> "Calon Honorer Tetap";
            case 4 -> "Honorer Tetap";
            case 5 -> "Non Pegawai";
            default -> "Invalid";
        };
    }
}
