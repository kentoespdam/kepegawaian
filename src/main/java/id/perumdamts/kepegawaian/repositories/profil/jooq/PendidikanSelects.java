package id.perumdamts.kepegawaian.repositories.profil.jooq;

import org.jooq.Field;

import java.util.List;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.JenjangPendidikan.JENJANG_PENDIDIKAN;
import static id.perumdamts.kepegawaian.jooq.tables.Pendidikan.PENDIDIKAN;

public final class PendidikanSelects {

    public static final List<Field<?>> COLUMNS = List.of(
            PENDIDIKAN.ID,
            PENDIDIKAN.BIODATA_ID,
            PENDIDIKAN.JENJANG_ID.as("self_jenjang_id"),
            PENDIDIKAN.GELAR_DEPAN,
            PENDIDIKAN.GELAR_BELAKANG,
            PENDIDIKAN.JURUSAN,
            PENDIDIKAN.INSTITUSI,
            PENDIDIKAN.KOTA,
            PENDIDIKAN.TAHUN_MASUK,
            PENDIDIKAN.IS_LULUS,
            PENDIDIKAN.TAHUN_LULUS,
            PENDIDIKAN.GPA,
            PENDIDIKAN.IS_LATEST,
            PENDIDIKAN.DISETUJUI,
            PENDIDIKAN.TANGGAL_PENGAJUAN,
            PENDIDIKAN.TANGGAL_DISETUJUI,
            PENDIDIKAN.DISETUJUI_OLEH,
            PENDIDIKAN.CHANGED_STATUS,
            BIODATA.NIK.as("biodata_nik"),
            BIODATA.NAMA.as("biodata_nama"),
            JENJANG_PENDIDIKAN.ID.as("jenjang_id"),
            JENJANG_PENDIDIKAN.NAMA.as("jenjang_nama"),
            JENJANG_PENDIDIKAN.SHORT_NAME.as("jenjang_short_name"),
            JENJANG_PENDIDIKAN.SEQ.as("jenjang_seq"),
            JENJANG_PENDIDIKAN.IS_STATISTIK.as("jenjang_is_statistik")
    );

    private PendidikanSelects() {
    }
}
