package id.perumdamts.kepegawaian.mapper.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.DukResponse;
import org.jooq.Record;

import java.time.LocalDate;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.Golongan.GOLONGAN;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;

public class DukRecordMapper {
    public static DukResponse map(Record r) {
        return new DukResponse(
                r.get(BIODATA.NAMA),
                r.get(PEGAWAI.NIPAM),
                r.get(GOLONGAN.GOLONGAN_),
                r.get(GOLONGAN.PANGKAT),
                r.get(PEGAWAI.TMT_GOLONGAN),
                r.get("nama_jabatan", String.class),
                r.get(PEGAWAI.TMT_JABATAN),
                r.get(PEGAWAI.TMT_KERJA),
                r.get("mk_tahun", Integer.class),
                r.get("mk_bulan", Integer.class),
                r.get("usia", Integer.class),
                r.get("jurusan", String.class),
                r.get("tahun_lulus", Integer.class),
                r.get("tingkat_pendidikan", String.class),
                r.get(PEGAWAI.STATUS_PEGAWAI)
        );
    }
}
