package id.perumdamts.kepegawaian.mapper.profil.kartuIdentitas;

import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasQuery;
import org.jooq.Record;
import org.jooq.RecordMapper;

import java.time.LocalDate;

public final class KartuIdentitasMultisetJooqMapper implements RecordMapper<Record, KartuIdentitasQuery> {
    public static final KartuIdentitasMultisetJooqMapper INSTANCE = new KartuIdentitasMultisetJooqMapper();

    private KartuIdentitasMultisetJooqMapper() {}

    @Override
    public KartuIdentitasQuery map(Record record) {
        return new KartuIdentitasQuery(
                record.get("id", Long.class),
                record.get("self_nik", String.class),
                null,
                null,
                null,
                null,
                record.get("nomor_kartu", String.class),
                record.get("tanggal_expired", LocalDate.class),
                record.get("tanggal_terima", LocalDate.class),
                record.get("notes", String.class),
                record.get("changed_status", Byte.class)
        );
    }
}
