package id.perumdamts.kepegawaian.mapper.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.SoResponse;
import org.jooq.Record;

public class SoRecordMapper {
    public static SoResponse map(Record r) {
        return new SoResponse(
                r.get("key", Long.class),
                r.get("boss", Long.class),
                r.get("level", Integer.class),
                r.get("jabatan", String.class),
                r.get("name", String.class),
                r.get("nik", String.class),
                java.util.List.of()
        );
    }
}
