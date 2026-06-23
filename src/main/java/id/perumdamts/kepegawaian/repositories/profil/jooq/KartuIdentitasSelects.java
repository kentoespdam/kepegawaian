package id.perumdamts.kepegawaian.repositories.profil.jooq;

import org.jooq.Field;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.JenisKitas.JENIS_KITAS;
import static id.perumdamts.kepegawaian.jooq.tables.KartuIdentitas.KARTU_IDENTITAS;

final class KartuIdentitasSelects {
    private KartuIdentitasSelects() {}

    static final Field<?>[] COLUMNS = new Field<?>[]{
            KARTU_IDENTITAS.ID,
            KARTU_IDENTITAS.NIK.as("self_nik"),
            KARTU_IDENTITAS.JENIS_KITAS_ID.as("self_jenis_kitas_id"),
            KARTU_IDENTITAS.NOMOR_KARTU,
            KARTU_IDENTITAS.TANGGAL_EXPIRED,
            KARTU_IDENTITAS.TANGGAL_TERIMA,
            KARTU_IDENTITAS.NOTES,
            KARTU_IDENTITAS.CHANGED_STATUS,
            BIODATA.NIK.as("biodata_nik"),
            BIODATA.NAMA.as("biodata_nama"),
            JENIS_KITAS.ID.as("jenis_kartu_id"),
            JENIS_KITAS.NAMA.as("jenis_kartu_nama")
    };
}
