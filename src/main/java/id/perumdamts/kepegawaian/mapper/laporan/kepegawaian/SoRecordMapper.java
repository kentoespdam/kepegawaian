package id.perumdamts.kepegawaian.mapper.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.SoResponse;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;

public class SoRecordMapper {
    public static SoResponse map(Record r) {
        return new SoResponse(
                r.get(JABATAN.ID),
                r.get(JABATAN.PARENT_ID),
                r.get(JABATAN.LEVEL_ID, Integer.class),
                r.get(JABATAN.NAMA),
                r.get("name", String.class),
                r.get("nik", String.class),
                java.util.List.of()
        );
    }
}
