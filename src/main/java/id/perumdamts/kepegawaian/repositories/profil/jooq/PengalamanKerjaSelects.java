package id.perumdamts.kepegawaian.repositories.profil.jooq;

import org.jooq.Field;
import org.jooq.impl.DSL;

import java.util.List;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.PengalamanKerja.PENGALAMAN_KERJA;

public class PengalamanKerjaSelects {
    private PengalamanKerjaSelects() {}

    public static final List<Field<?>> COLUMNS = List.of(
            PENGALAMAN_KERJA.ID,
            PENGALAMAN_KERJA.BIODATA_ID,
            BIODATA.NIK.as("biodata_nik"),
            BIODATA.NAMA.as("biodata_nama"),
            PENGALAMAN_KERJA.NAMA_PERUSAHAAN,
            PENGALAMAN_KERJA.TYPE_PERUSAHAAN,
            PENGALAMAN_KERJA.JABATAN,
            PENGALAMAN_KERJA.LOKASI,
            PENGALAMAN_KERJA.TAHUN_MASUK,
            PENGALAMAN_KERJA.TAHUN_KELUAR,
            PENGALAMAN_KERJA.NOTES,
            PENGALAMAN_KERJA.CHANGED_STATUS
    );
}
