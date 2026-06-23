package id.perumdamts.kepegawaian.repositories.profil.jooq;

import org.jooq.Field;

import java.util.List;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.JenisPelatihan.JENIS_PELATIHAN;
import static id.perumdamts.kepegawaian.jooq.tables.Pelatihan.PELATIHAN;

public final class PelatihanSelects {
    public static final List<Field<?>> COLUMNS = List.of(
            PELATIHAN.ID,
            PELATIHAN.BIODATA_ID,
            PELATIHAN.JENIS_PELATIHAN_ID.as("self_jenis_pelatihan_id"),
            PELATIHAN.NAMA,
            PELATIHAN.LEMBAGA,
            PELATIHAN.TANGGAL_MULAI,
            PELATIHAN.TANGGAL_SELESAI,
            PELATIHAN.LULUS,
            PELATIHAN.NILAI,
            PELATIHAN.IKATAN_DINAS,
            PELATIHAN.TANGGAL_AKHIR_IKATAN,
            PELATIHAN.NOTES,
            PELATIHAN.CHANGED_STATUS,
            BIODATA.NIK.as("biodata_nik"),
            BIODATA.NAMA.as("biodata_nama"),
            JENIS_PELATIHAN.ID.as("jenis_pelatihan_id"),
            JENIS_PELATIHAN.NAMA.as("jenis_pelatihan_nama")
    );

    private PelatihanSelects() {
    }
}