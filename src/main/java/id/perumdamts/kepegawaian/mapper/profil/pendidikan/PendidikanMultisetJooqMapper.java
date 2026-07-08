package id.perumdamts.kepegawaian.mapper.profil.pendidikan;

import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanQuery;
import org.jooq.Record;
import org.jooq.RecordMapper;

public final class PendidikanMultisetJooqMapper implements RecordMapper<Record, PendidikanQuery> {
    public static final PendidikanMultisetJooqMapper INSTANCE = new PendidikanMultisetJooqMapper();

    private PendidikanMultisetJooqMapper() {}

    @Override
    public PendidikanQuery map(Record record) {
        Byte isLatestRaw = record.get("is_latest", Byte.class);
        return new PendidikanQuery(
                record.get("id", Long.class),
                record.get("biodata_id", String.class),
                null,
                null,
                null,
                null,
                record.get("gelar_depan", String.class),
                record.get("gelar_belakang", String.class),
                record.get("jurusan", String.class),
                record.get("institusi", String.class),
                record.get("kota", String.class),
                record.get("tahun_masuk", Integer.class),
                record.get("is_lulus", Boolean.class),
                record.get("tahun_lulus", Integer.class),
                record.get("gpa", Double.class),
                isLatestRaw != null && isLatestRaw == 1,
                record.get("changed_status", Byte.class)
        );
    }
}
