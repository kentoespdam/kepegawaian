package id.perumdamts.kepegawaian.mapper.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.DukResponse;
import org.jooq.Record;

import java.time.LocalDate;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.Golongan.GOLONGAN;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.JenjangPendidikan.JENJANG_PENDIDIKAN;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;
import static id.perumdamts.kepegawaian.jooq.tables.Pendidikan.PENDIDIKAN;

public class DukRecordMapper {
    public static DukResponse map(Record r) {
        LocalDate tmtGol = r.get(PEGAWAI.TMT_GOLONGAN);
        LocalDate tmtKerja = r.get(PEGAWAI.TMT_KERJA);
        LocalDate tglLahir = r.get(BIODATA.TANGGAL_LAHIR);

        return new DukResponse(
                r.get(BIODATA.NAMA),
                r.get(PEGAWAI.NIPAM),
                r.get(GOLONGAN.GOLONGAN_),
                r.get(GOLONGAN.PANGKAT),
                tmtGol,
                r.get(JABATAN.NAMA),
                r.get(PEGAWAI.TMT_JABATAN),
                tmtKerja,
                r.get("mk_tahun", Integer.class),
                r.get("mk_bulan", Integer.class),
                r.get("usia", Integer.class),
                r.get(PENDIDIKAN.JURUSAN),
                r.get(PENDIDIKAN.TAHUN_LULUS),
                r.get(JENJANG_PENDIDIKAN.NAMA),
                r.get(PEGAWAI.STATUS_PEGAWAI)
        );
    }
}
