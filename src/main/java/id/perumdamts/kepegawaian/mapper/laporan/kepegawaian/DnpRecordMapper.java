package id.perumdamts.kepegawaian.mapper.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.DnpResponse;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.Golongan.GOLONGAN;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;

public class DnpRecordMapper {
    public static DnpResponse map(Record r) {
        return new DnpResponse(
                r.get("kode_organisasi", String.class),
                r.get("level_jabatan", Integer.class),
                r.get(BIODATA.NAMA),
                r.get(PEGAWAI.NIPAM),
                r.get("nama_jabatan", String.class),
                r.get("tmt_jabatan", String.class),
                r.get(GOLONGAN.PANGKAT),
                r.get(GOLONGAN.GOLONGAN_),
                r.get("tmt_golongan", String.class),
                r.get("mkg_tahun", Integer.class),
                r.get("mkg_bulan", Integer.class),
                r.get("tmt_kerja", String.class),
                r.get("mk_tahun", Integer.class),
                r.get("mk_bulan", Integer.class),
                r.get("pendidikan", String.class),
                r.get("ttl", String.class)
        );
    }
}
