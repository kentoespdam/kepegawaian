package id.perumdamts.kepegawaian.mapper.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.SoResponse;
import org.jooq.Record;

public class SoRecordMapper {
    public static SoResponse map(Record r) {
        String namaPegawai = r.get("nama_pegawai", String.class);
        boolean isLowong = namaPegawai == null || namaPegawai.isBlank();

        return SoResponse.flat(
                r.get("id", Long.class),
                r.get("parent_id", Long.class),
                r.get("kode_jabatan", String.class),
                r.get("nama_jabatan", String.class),
                r.get("level", Integer.class),
                namaPegawai,
                r.get("nipam", String.class),
                r.get("nama_organisasi", String.class),
                r.get("foto_profil", String.class),
                isLowong
        );
    }
}
