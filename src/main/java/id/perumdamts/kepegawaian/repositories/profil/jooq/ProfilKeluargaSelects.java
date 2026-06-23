package id.perumdamts.kepegawaian.repositories.profil.jooq;

import org.jooq.Field;

import java.util.List;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.JenjangPendidikan.JENJANG_PENDIDIKAN;
import static id.perumdamts.kepegawaian.jooq.tables.ProfilKeluarga.PROFIL_KELUARGA;

public final class ProfilKeluargaSelects {

    public static final List<Field<?>> COLUMNS = List.of(
            PROFIL_KELUARGA.ID,
            PROFIL_KELUARGA.BIODATA_ID,
            PROFIL_KELUARGA.NIK,
            PROFIL_KELUARGA.NAMA,
            PROFIL_KELUARGA.JENIS_KELAMIN,
            PROFIL_KELUARGA.AGAMA,
            PROFIL_KELUARGA.HUBUNGAN_KELUARGA,
            PROFIL_KELUARGA.TEMPAT_LAHIR,
            PROFIL_KELUARGA.TANGGAL_LAHIR,
            PROFIL_KELUARGA.TANGGUNGAN,
            PROFIL_KELUARGA.PENDIDIKAN_ID.as("self_pendidikan_id"),
            PROFIL_KELUARGA.STATUS_PENDIDIKAN,
            PROFIL_KELUARGA.STATUS_KAWIN,
            PROFIL_KELUARGA.NOTES,
            PROFIL_KELUARGA.VERSION,
            PROFIL_KELUARGA.IS_DELETED,
            PROFIL_KELUARGA.CHANGED_STATUS,
            BIODATA.NIK.as("biodata_nik"),
            BIODATA.NAMA.as("biodata_nama"),
            JENJANG_PENDIDIKAN.ID.as("pendidikan_id"),
            JENJANG_PENDIDIKAN.NAMA.as("pendidikan_nama"),
            JENJANG_PENDIDIKAN.SHORT_NAME.as("pendidikan_short_name"),
            JENJANG_PENDIDIKAN.SEQ.as("pendidikan_seq"),
            JENJANG_PENDIDIKAN.IS_STATISTIK.as("pendidikan_is_statistik")
    );

    private ProfilKeluargaSelects() {
    }
}