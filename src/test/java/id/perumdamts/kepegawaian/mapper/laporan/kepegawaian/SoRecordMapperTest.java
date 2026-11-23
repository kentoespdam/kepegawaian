package id.perumdamts.kepegawaian.mapper.laporan.kepegawaian;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;
import static org.junit.jupiter.api.Assertions.*;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.SoResponse;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

class SoRecordMapperTest {

    @Test
    void mapReadsAliasedFields() {
        var record = DSL.using(org.jooq.SQLDialect.MYSQL)
                .newRecord(
                        JABATAN.ID.as("id"),
                        JABATAN.PARENT_ID.as("parent_id"),
                        JABATAN.KODE.as("kode_jabatan"),
                        JABATAN.NAMA.as("nama_jabatan"),
                        JABATAN.LEVEL_ID.as("level"),
                        BIODATA.NAMA.as("nama_pegawai"),
                        PEGAWAI.NIPAM.as("nipam"),
                        ORGANISASI.NAMA.as("nama_organisasi"),
                        BIODATA.FOTO_PROFIL.as("foto_profil")
                );

        record.set(JABATAN.ID.as("id"), 10L);
        record.set(JABATAN.PARENT_ID.as("parent_id"), 5L);
        record.set(JABATAN.KODE.as("kode_jabatan"), "MGR-01");
        record.set(JABATAN.NAMA.as("nama_jabatan"), "Manager");
        record.set(JABATAN.LEVEL_ID.as("level"), 3L);
        record.set(BIODATA.NAMA.as("nama_pegawai"), "Budi");
        record.set(PEGAWAI.NIPAM.as("nipam"), "8903002");
        record.set(ORGANISASI.NAMA.as("nama_organisasi"), "Bagian Umum");
        record.set(BIODATA.FOTO_PROFIL.as("foto_profil"), "budi.jpg");

        SoResponse result = SoRecordMapper.map(record);

        assertEquals(10L, result.id());
        assertEquals(5L, result.parentId());
        assertEquals("MGR-01", result.kodeJabatan());
        assertEquals("Manager", result.namaJabatan());
        assertEquals(3, result.level());
        assertEquals("Budi", result.namaPegawai());
        assertEquals("8903002", result.nipam());
        assertEquals("Bagian Umum", result.namaOrganisasi());
        assertEquals("budi.jpg", result.fotoProfil());
        assertFalse(result.isLowong());
        assertNull(result.children());
    }

    @Test
    void mapHandlesVacancyWhenPegawaiNullOrBlank() {
        var record = DSL.using(org.jooq.SQLDialect.MYSQL)
                .newRecord(
                        JABATAN.ID.as("id"),
                        JABATAN.PARENT_ID.as("parent_id"),
                        JABATAN.KODE.as("kode_jabatan"),
                        JABATAN.NAMA.as("nama_jabatan"),
                        JABATAN.LEVEL_ID.as("level"),
                        BIODATA.NAMA.as("nama_pegawai"),
                        PEGAWAI.NIPAM.as("nipam"),
                        ORGANISASI.NAMA.as("nama_organisasi"),
                        BIODATA.FOTO_PROFIL.as("foto_profil")
                );

        record.set(JABATAN.ID.as("id"), 20L);
        record.set(JABATAN.PARENT_ID.as("parent_id"), null);
        record.set(JABATAN.KODE.as("kode_jabatan"), "DIR-01");
        record.set(JABATAN.NAMA.as("nama_jabatan"), "Direktur Utama");
        record.set(JABATAN.LEVEL_ID.as("level"), 1L);
        record.set(BIODATA.NAMA.as("nama_pegawai"), null);
        record.set(PEGAWAI.NIPAM.as("nipam"), null);
        record.set(ORGANISASI.NAMA.as("nama_organisasi"), "Direksi");
        record.set(BIODATA.FOTO_PROFIL.as("foto_profil"), null);

        SoResponse result = SoRecordMapper.map(record);

        assertEquals(20L, result.id());
        assertNull(result.parentId());
        assertEquals("DIR-01", result.kodeJabatan());
        assertEquals("Direktur Utama", result.namaJabatan());
        assertEquals(1, result.level());
        assertNull(result.namaPegawai());
        assertNull(result.nipam());
        assertEquals("Direksi", result.namaOrganisasi());
        assertNull(result.fotoProfil());
        assertTrue(result.isLowong());
        assertNull(result.children());
    }
}
