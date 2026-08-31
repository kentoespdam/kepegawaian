package id.perumdamts.kepegawaian.repositories.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.DnpResponse;
import id.perumdamts.kepegawaian.mapper.laporan.kepegawaian.DnpRecordMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.Golongan.GOLONGAN;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.JenjangPendidikan.JENJANG_PENDIDIKAN;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;
import static id.perumdamts.kepegawaian.jooq.tables.Pendidikan.PENDIDIKAN;

@Repository
@RequiredArgsConstructor
public class DnpRepository {
    private final DSLContext dsl;

    public List<DnpResponse> fetch() {
        var tmtJabatan = DSL.field("DATE_FORMAT({0}, '%d.%m.%Y')", String.class, PEGAWAI.TMT_JABATAN);
        var tmtGolongan = DSL.field("DATE_FORMAT({0}, '%d.%m.%Y')", String.class, PEGAWAI.TMT_GOLONGAN);
        var tmtKerja = DSL.field("DATE_FORMAT({0}, '%d.%m.%Y')", String.class, PEGAWAI.TMT_KERJA);
        var mkgTahun = DSL.field("TIMESTAMPDIFF(YEAR, {0}, NOW())", Integer.class, PEGAWAI.TMT_GOLONGAN);
        var mkgBulan = DSL.field("TIMESTAMPDIFF(MONTH, {0}, NOW())", Integer.class, PEGAWAI.TMT_GOLONGAN);
        var mkTahun = DSL.field("TIMESTAMPDIFF(YEAR, {0}, NOW())", Integer.class, PEGAWAI.TMT_KERJA);
        var mkBulan = DSL.field("TIMESTAMPDIFF(MONTH, {0}, NOW())", Integer.class, PEGAWAI.TMT_KERJA);
        var pendidikan = DSL.field("CONCAT_WS(' ', {0}, {1}, {2})", String.class,
                JENJANG_PENDIDIKAN.NAMA, PENDIDIKAN.JURUSAN, PENDIDIKAN.TAHUN_LULUS);
        var ttl = DSL.field("CONCAT_WS(' ', {0}, DATE_FORMAT({1}, '%d.%m.%Y'))", String.class,
                BIODATA.TEMPAT_LAHIR, BIODATA.TANGGAL_LAHIR);

        return dsl.select(
                        ORGANISASI.KODE.as("kode_organisasi"),
                        JABATAN.LEVEL_ID.as("level_jabatan"),
                        BIODATA.NAMA, PEGAWAI.NIPAM, JABATAN.NAMA.as("nama_jabatan"),
                        tmtJabatan.as("tmt_jabatan"),
                        GOLONGAN.PANGKAT, GOLONGAN.GOLONGAN_,
                        tmtGolongan.as("tmt_golongan"),
                        mkgTahun.as("mkg_tahun"), mkgBulan.as("mkg_bulan"),
                        tmtKerja.as("tmt_kerja"),
                        mkTahun.as("mk_tahun"), mkBulan.as("mk_bulan"),
                        pendidikan.as("pendidikan"),
                        ttl.as("ttl")
                )
                .from(PEGAWAI)
                .join(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .join(JABATAN).on(PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(GOLONGAN).on(PEGAWAI.GOLONGAN_ID.eq(GOLONGAN.ID))
                .leftJoin(ORGANISASI).on(PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(PENDIDIKAN).on(BIODATA.NIK.eq(PENDIDIKAN.BIODATA_ID))
                .join(JENJANG_PENDIDIKAN).on(PENDIDIKAN.JENJANG_ID.eq(JENJANG_PENDIDIKAN.ID))
                        .and(BIODATA.PENDIDIKAN_ID.eq(JENJANG_PENDIDIKAN.ID))
                .where(PEGAWAI.STATUS_KERJA.in((byte) 1, (byte) 2)) // DIRUMAHKAN, KARYAWAN_AKTIF
                        .and(PENDIDIKAN.IS_LATEST.eq((byte) 1))
                .orderBy(ORGANISASI.KODE, JABATAN.LEVEL_ID, PEGAWAI.TMT_KERJA)
                .fetch(DnpRecordMapper::map);
    }

    public java.util.List<java.util.Map<String, Object>> fetchOrganisasiCodes(int maxLevel) {
        return dsl.select(ORGANISASI.KODE, ORGANISASI.NAMA)
                .from(ORGANISASI)
                .where(ORGANISASI.IS_DELETED.eq(false))
                .and(ORGANISASI.LEVEL_ORG.le(maxLevel))
                .fetch(r -> {
                    var map = new java.util.HashMap<String, Object>();
                    map.put("kode", r.get(ORGANISASI.KODE));
                    map.put("nama", r.get(ORGANISASI.NAMA));
                    return map;
                });
    }
}
