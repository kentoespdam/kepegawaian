package id.perumdamts.kepegawaian.mapper.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.DukResponse;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.Golongan.GOLONGAN;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;
import static id.perumdamts.kepegawaian.jooq.tables.Pendidikan.PENDIDIKAN;
import static org.junit.jupiter.api.Assertions.*;

class DukRecordMapperTest {

    @Test
    void mapReadsAliasedFields() {
        var mkTahun = DSL.field("TIMESTAMPDIFF(YEAR, {0}, NOW())", Integer.class, PEGAWAI.TMT_KERJA);
        var mkBulan = DSL.field("TIMESTAMPDIFF(MONTH, {0}, NOW())", Integer.class, PEGAWAI.TMT_KERJA);
        var usia = DSL.field("TIMESTAMPDIFF(YEAR, {0}, NOW())", Integer.class, BIODATA.TANGGAL_LAHIR);

        var record = DSL.using(org.jooq.SQLDialect.MYSQL)
                .newRecord(
                        BIODATA.NAMA, PEGAWAI.NIPAM,
                        GOLONGAN.GOLONGAN_, GOLONGAN.PANGKAT, PEGAWAI.TMT_GOLONGAN,
                        DSL.field("nama_jabatan", String.class), PEGAWAI.TMT_JABATAN, PEGAWAI.TMT_KERJA,
                        mkTahun.as("mk_tahun"), mkBulan.as("mk_bulan"), usia.as("usia"),
                        PENDIDIKAN.JURUSAN, PENDIDIKAN.TAHUN_LULUS,
                        DSL.field("tingkat_pendidikan", String.class),
                        PEGAWAI.STATUS_PEGAWAI
                );

        record.set(BIODATA.NAMA, "Budi");
        record.set(PEGAWAI.NIPAM, "8903002");
        record.set(GOLONGAN.GOLONGAN_, "IV/a");
        record.set(GOLONGAN.PANGKAT, "Pembina");
        record.set(PEGAWAI.TMT_GOLONGAN, LocalDate.of(2020, 1, 1));
        record.set(DSL.field("nama_jabatan", String.class), "Manager");
        record.set(PEGAWAI.TMT_JABATAN, LocalDate.of(2021, 6, 1));
        record.set(PEGAWAI.TMT_KERJA, LocalDate.of(2015, 3, 15));
        record.set(mkTahun.as("mk_tahun"), 11);
        record.set(mkBulan.as("mk_bulan"), 133);
        record.set(usia.as("usia"), 30);
        record.set(PENDIDIKAN.JURUSAN, "Teknik Informatika");
        record.set(PENDIDIKAN.TAHUN_LULUS, 2014);
        record.set(DSL.field("tingkat_pendidikan", String.class), "S1");
        record.set(PEGAWAI.STATUS_PEGAWAI, (byte) 2);

        DukResponse result = DukRecordMapper.map(record);

        assertEquals("Budi", result.nama());
        assertEquals("8903002", result.nipam());
        assertEquals("IV/a", result.golongan());
        assertEquals("Pembina", result.pangkat());
        assertEquals(LocalDate.of(2020, 1, 1), result.tmtGolongan());
        assertEquals("Manager", result.namaJabatan());
        assertEquals(LocalDate.of(2021, 6, 1), result.tmtJabatan());
        assertEquals(LocalDate.of(2015, 3, 15), result.tmtKerja());
        assertEquals(11, result.mkTahun());
        assertEquals(133, result.mkBulan());
        assertEquals(30, result.usia());
        assertEquals("Teknik Informatika", result.jurusan());
        assertEquals(2014, result.tahunLulus());
        assertEquals("S1", result.tingkatPendidikan());
        assertEquals((byte) 2, result.statusPegawai());
    }
}
