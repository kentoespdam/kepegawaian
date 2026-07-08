package id.perumdamts.kepegawaian.mapper.profil.kartuIdentitas;

import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasQuery;
import org.jooq.Record;
import org.jooq.RecordMapper;

import java.util.Objects;

public final class KartuIdentitasJooqMapper implements RecordMapper<Record, KartuIdentitasQuery> {
    public static final KartuIdentitasJooqMapper INSTANCE = new KartuIdentitasJooqMapper();

    private KartuIdentitasJooqMapper() {}
    @Override
    public KartuIdentitasQuery map(Record record) {
        return new KartuIdentitasQuery(
                record.get("id", Long.class),
                record.get("self_nik", String.class),
                record.get("biodata_nik", String.class),
                record.get("biodata_nama", String.class),
                Objects.requireNonNullElse(record.get("jenis_kartu_id", Long.class), record.get("self_jenis_kitas_id", Long.class)),
                record.get("jenis_kartu_nama", String.class),
                record.get("nomor_kartu", String.class),
                record.get("tanggal_expired", java.time.LocalDate.class),
                record.get("tanggal_terima", java.time.LocalDate.class),
                record.get("notes", String.class),
                record.get("changed_status", Byte.class)
        );
    }
}
