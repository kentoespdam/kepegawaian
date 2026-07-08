package id.perumdamts.kepegawaian.mapper.profil.pelatihan;

import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanQuery;
import org.jooq.Record;
import org.jooq.RecordMapper;

import java.util.Objects;

public final class PelatihanJooqMapper implements RecordMapper<Record, PelatihanQuery> {
    public static final PelatihanJooqMapper INSTANCE = new PelatihanJooqMapper();

    private PelatihanJooqMapper() {}
    @Override
    public PelatihanQuery map(Record record) {
        Long jenisPelatihanId = record.get("jenis_pelatihan_id", Long.class);
        return new PelatihanQuery(
                record.get("id", Long.class),
                record.get("biodata_id", String.class),
                record.get("biodata_nik", String.class),
                record.get("biodata_nama", String.class),
                Objects.requireNonNullElse(record.get("self_jenis_pelatihan_id", Long.class), jenisPelatihanId),
                record.get("jenis_pelatihan_nama", String.class),
                record.get("nama", String.class),
                record.get("lembaga", String.class),
                record.get("tanggal_mulai", java.time.LocalDate.class),
                record.get("tanggal_selesai", java.time.LocalDate.class),
                record.get("lulus", Boolean.class),
                record.get("nilai", String.class),
                record.get("ikatan_dinas", Boolean.class),
                record.get("tanggal_akhir_ikatan", java.time.LocalDate.class),
                record.get("notes", String.class),
                record.get("changed_status", Byte.class)
        );
    }
}