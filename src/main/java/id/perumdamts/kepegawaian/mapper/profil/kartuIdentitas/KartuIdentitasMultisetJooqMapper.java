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
        KartuIdentitasQuery q = new KartuIdentitasQuery();
        q.setId(record.get("id", Long.class));
        q.setBiodataId(record.get("self_nik", String.class));
        q.setNomorKartu(record.get("nomor_kartu", String.class));
        q.setTanggalExpired(record.get("tanggal_expired", LocalDate.class));
        q.setTanggalTerima(record.get("tanggal_terima", LocalDate.class));
        q.setNotes(record.get("notes", String.class));
        q.setChangedStatus(record.get("changed_status", Byte.class));
        return q;
    }
}
