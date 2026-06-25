package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataDetail;
import id.perumdamts.kepegawaian.entities.commons.EAgama;
import id.perumdamts.kepegawaian.entities.commons.EGolonganDarah;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.jooq.enums.BiodataGolonganDarah;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Regression: GET /profil/biodata/{id} returned 500 "An error occurred when mapping
 * record to class ...BiodataDetail". BiodataDetailQuery used fetchOptionalInto, whose
 * default JOOQ mapper converts enums by NAME, but jenis_kelamin/agama/status_kawin are
 * TINYINT ordinals (Byte) -> could not coerce to enum. Mapper must convert by ordinal.
 * See bd kepegawaian-9qo.
 */
class BiodataDetailRowMapperTest {

    private final DSLContext dsl = DSL.using(SQLDialect.MARIADB);

    private static final Field<String> NIK = DSL.field("nik", String.class);
    private static final Field<String> NAMA = DSL.field("nama", String.class);
    private static final Field<Byte> JENIS_KELAMIN = DSL.field("jenis_kelamin", Byte.class);
    private static final Field<String> TEMPAT_LAHIR = DSL.field("tempat_lahir", String.class);
    private static final Field<java.time.LocalDate> TANGGAL_LAHIR = DSL.field("tanggal_lahir", java.time.LocalDate.class);
    private static final Field<String> ALAMAT = DSL.field("alamat", String.class);
    private static final Field<String> TELP = DSL.field("telp", String.class);
    private static final Field<Byte> AGAMA = DSL.field("agama", Byte.class);
    private static final Field<String> IBU_KANDUNG = DSL.field("ibu_kandung", String.class);
    private static final Field<Long> PENDIDIKAN_ID = DSL.field("pendidikan_id", Long.class);
    private static final Field<BiodataGolonganDarah> GOLONGAN_DARAH = DSL.field("golongan_darah", BiodataGolonganDarah.class);
    private static final Field<Byte> STATUS_KAWIN = DSL.field("status_kawin", Byte.class);
    private static final Field<String> FOTO_PROFIL = DSL.field("foto_profil", String.class);
    private static final Field<String> NOTES = DSL.field("notes", String.class);
    private static final Field<Boolean> IS_PEGAWAI = DSL.field("is_pegawai", Boolean.class);

    private Record newRow() {
        return dsl.newRecord(NIK, NAMA, JENIS_KELAMIN, TEMPAT_LAHIR, TANGGAL_LAHIR,
                ALAMAT, TELP, AGAMA, IBU_KANDUNG, PENDIDIKAN_ID, GOLONGAN_DARAH,
                STATUS_KAWIN, FOTO_PROFIL, NOTES, IS_PEGAWAI);
    }

    @Test
    void mapsByteOrdinalsToEnums() {
        Record row = newRow();
        row.set(NIK, "1234567890");
        row.set(NAMA, "Budi");
        row.set(JENIS_KELAMIN, (byte) 1);   // PEREMPUAN
        row.set(AGAMA, (byte) 1);            // ISLAM
        row.set(GOLONGAN_DARAH, BiodataGolonganDarah.AB);
        row.set(STATUS_KAWIN, (byte) 2);     // JANDA_DUDA
        row.set(IS_PEGAWAI, true);

        BiodataDetail d = assertDoesNotThrow(
                () -> BiodataDetailRowMapper.map(row, List.of(), List.of()),
                "map() must not throw on byte-ordinal enum columns");

        assertEquals("1234567890", d.nik());
        assertEquals(EJenisKelamin.PEREMPUAN, d.jenisKelamin());
        assertEquals(EAgama.ISLAM, d.agama());
        assertEquals(EGolonganDarah.AB, d.golonganDarah());
        assertEquals(EStatusKawin.JANDA_DUDA, d.statusKawin());
        assertEquals(Boolean.TRUE, d.isPegawai());
        assertEquals(List.of(), d.pendidikan());
        assertEquals(List.of(), d.kartuIdentitas());
    }

    @Test
    void mapsNullEnumColumnsToNull() {
        Record row = newRow();
        row.set(NIK, "1234567890");
        row.set(JENIS_KELAMIN, (Byte) null);
        row.set(AGAMA, (Byte) null);
        row.set(GOLONGAN_DARAH, (BiodataGolonganDarah) null);
        row.set(STATUS_KAWIN, (Byte) null);

        BiodataDetail d = assertDoesNotThrow(
                () -> BiodataDetailRowMapper.map(row, List.of(), List.of()),
                "map() must be null-safe for absent enum columns");

        assertNull(d.jenisKelamin());
        assertNull(d.agama());
        assertNull(d.golonganDarah());
        assertNull(d.statusKawin());
    }

    @Test
    void mapsFirstOrdinalEnumConstants() {
        Record row = newRow();
        row.set(JENIS_KELAMIN, (byte) 0);   // LAKI_LAKI
        row.set(AGAMA, (byte) 0);            // TIDAK_TAHU
        row.set(STATUS_KAWIN, (byte) 0);     // BELUM_KAWIN

        BiodataDetail d = BiodataDetailRowMapper.map(row, List.of(), List.of());

        assertEquals(EJenisKelamin.LAKI_LAKI, d.jenisKelamin());
        assertEquals(EAgama.TIDAK_TAHU, d.agama());
        assertEquals(EStatusKawin.BELUM_KAWIN, d.statusKawin());
    }
}
