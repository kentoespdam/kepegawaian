package id.perumdamts.kepegawaian.repositories.profil.jooq;

import org.jooq.Field;

import java.util.List;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.JenisKeahlian.JENIS_KEAHLIAN;
import static id.perumdamts.kepegawaian.jooq.tables.Keahlian.KEAHLIAN;

public final class KeahlianSelects {

    public static final List<Field<?>> COLUMNS = List.of(
            KEAHLIAN.ID,
            KEAHLIAN.BIODATA_ID,
            KEAHLIAN.JENIS_KEAHLIAN_ID.as("self_jenis_keahlian_id"),
            KEAHLIAN.KUALIFIKASI,
            KEAHLIAN.SERTIFIKASI,
            KEAHLIAN.INSTITUSI,
            KEAHLIAN.TAHUN,
            KEAHLIAN.MASA_BERLAKU,
            KEAHLIAN.DISETUJUI,
            KEAHLIAN.TANGGAL_PENGAJUAN,
            KEAHLIAN.TANGGAL_DISETUJUI,
            KEAHLIAN.DISETUJUI_OLEH,
            KEAHLIAN.CHANGED_STATUS,
            BIODATA.NIK.as("biodata_nik"),
            BIODATA.NAMA.as("biodata_nama"),
            JENIS_KEAHLIAN.ID.as("jenis_keahlian_id"),
            JENIS_KEAHLIAN.NAMA.as("jenis_keahlian_nama")
    );

    private KeahlianSelects() {}
}
