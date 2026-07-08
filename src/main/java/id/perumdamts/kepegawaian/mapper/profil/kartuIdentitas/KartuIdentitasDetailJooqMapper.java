package id.perumdamts.kepegawaian.mapper.profil.kartuIdentitas;

import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasDetail;
import org.jooq.Record;
import org.jooq.RecordMapper;

import java.util.List;

public final class KartuIdentitasDetailJooqMapper implements RecordMapper<Record, KartuIdentitasDetail> {
    public static final KartuIdentitasDetailJooqMapper INSTANCE = new KartuIdentitasDetailJooqMapper();

    private KartuIdentitasDetailJooqMapper() {}

    @SuppressWarnings("unchecked")
    @Override
    public KartuIdentitasDetail map(Record record) {
        return new KartuIdentitasDetail(
                record.get("id", Long.class),
                record.get("self_nik", String.class),
                record.get("biodata_nik", String.class),
                record.get("biodata_nama", String.class),
                record.get("jenis_kartu_id", Long.class),
                record.get("jenis_kartu_nama", String.class),
                record.get("nomor_kartu", String.class),
                record.get("tanggal_expired", java.time.LocalDate.class),
                record.get("tanggal_terima", java.time.LocalDate.class),
                record.get("notes", String.class),
                record.get("changed_status", Byte.class),
                record.get("lampiran", List.class)
        );
    }
}
