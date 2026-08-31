package id.perumdamts.kepegawaian.mapper.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.DnpResponse;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.Golongan.GOLONGAN;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;
import static org.junit.jupiter.api.Assertions.*;

class DnpRecordMapperTest {

    @Test
    void mapReadsAliasedFields() {
        var record = DSL.using(org.jooq.SQLDialect.MYSQL)
                .newRecord(
                        DSL.field("kode_organisasi", String.class),
                        DSL.field("level_jabatan", Integer.class),
                        BIODATA.NAMA, PEGAWAI.NIPAM,
                        DSL.field("nama_jabatan", String.class),
                        DSL.field("tmt_jabatan", String.class),
                        GOLONGAN.PANGKAT, GOLONGAN.GOLONGAN_,
                        DSL.field("tmt_golongan", String.class),
                        DSL.field("mkg_tahun", Integer.class),
                        DSL.field("mkg_bulan", Integer.class),
                        DSL.field("tmt_kerja", String.class),
                        DSL.field("mk_tahun", Integer.class),
                        DSL.field("mk_bulan", Integer.class),
                        DSL.field("pendidikan", String.class),
                        DSL.field("ttl", String.class)
                );

        record.set(DSL.field("kode_organisasi", String.class), "ORG001");
        record.set(DSL.field("level_jabatan", Integer.class), 3);
        record.set(BIODATA.NAMA, "Budi");
        record.set(PEGAWAI.NIPAM, "8903002");
        record.set(DSL.field("nama_jabatan", String.class), "Manager");
        record.set(DSL.field("tmt_jabatan", String.class), "01.06.2021");
        record.set(GOLONGAN.PANGKAT, "Pembina");
        record.set(GOLONGAN.GOLONGAN_, "IV/a");
        record.set(DSL.field("tmt_golongan", String.class), "01.01.2020");
        record.set(DSL.field("mkg_tahun", Integer.class), 6);
        record.set(DSL.field("mkg_bulan", Integer.class), 72);
        record.set(DSL.field("tmt_kerja", String.class), "15.03.2015");
        record.set(DSL.field("mk_tahun", Integer.class), 11);
        record.set(DSL.field("mk_bulan", Integer.class), 133);
        record.set(DSL.field("pendidikan", String.class), "S1 Teknik Informatika 2014");
        record.set(DSL.field("ttl", String.class), "Jakarta 01.01.1990");

        DnpResponse result = DnpRecordMapper.map(record);

        assertEquals("ORG001", result.kodeOrganisasi());
        assertEquals(3, result.levelJabatan());
        assertEquals("Budi", result.nama());
        assertEquals("8903002", result.nipam());
        assertEquals("Manager", result.namaJabatan());
        assertEquals("01.06.2021", result.tmtJabatan());
        assertEquals("Pembina", result.pangkat());
        assertEquals("IV/a", result.golongan());
        assertEquals("01.01.2020", result.tmtGolongan());
        assertEquals(6, result.mkgTahun());
        assertEquals(72, result.mkgBulan());
        assertEquals("15.03.2015", result.tmtKerja());
        assertEquals(11, result.mkTahun());
        assertEquals(133, result.mkBulan());
        assertEquals("S1 Teknik Informatika 2014", result.pendidikan());
        assertEquals("Jakarta 01.01.1990", result.ttl());
    }
}
