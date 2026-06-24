package id.perumdamts.kepegawaian.repositories.profil.jooq;

import org.jooq.Field;

import java.util.List;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.JenjangPendidikan.JENJANG_PENDIDIKAN;

public final class BiodataSelects {

    public static final List<Field<?>> COLUMNS = List.of(
            BIODATA.NIK,
            BIODATA.NAMA,
            BIODATA.JENIS_KELAMIN,
            BIODATA.TEMPAT_LAHIR,
            BIODATA.TANGGAL_LAHIR,
            BIODATA.ALAMAT,
            BIODATA.TELP,
            BIODATA.AGAMA,
            BIODATA.IBU_KANDUNG,
            BIODATA.PENDIDIKAN_ID.as("self_pendidikan_terakhir_id"),
            BIODATA.GOLONGAN_DARAH,
            BIODATA.STATUS_KAWIN,
            BIODATA.FOTO_PROFIL,
            BIODATA.NOTES,
            BIODATA.IS_PEGAWAI,
            JENJANG_PENDIDIKAN.ID.as("pendidikan_terakhir_id"),
            JENJANG_PENDIDIKAN.NAMA.as("pendidikan_terakhir_nama"),
            JENJANG_PENDIDIKAN.SHORT_NAME.as("pendidikan_terakhir_short_name"),
            JENJANG_PENDIDIKAN.SEQ.as("pendidikan_terakhir_seq"),
            JENJANG_PENDIDIKAN.IS_STATISTIK.as("pendidikan_terakhir_is_statistik")
    );

    private BiodataSelects() {
    }
}
