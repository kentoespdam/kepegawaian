package id.perumdamts.kepegawaian.mapper.profil.pendidikan;

import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanResponse;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanQuery;
import org.jooq.Record;
import org.jooq.RecordMapper;

import java.time.LocalDateTime;

public final class PendidikanJooqMapper implements RecordMapper<Record, PendidikanQuery> {
    public static final PendidikanJooqMapper INSTANCE = new PendidikanJooqMapper();

    private PendidikanJooqMapper() {}

    @Override
    public PendidikanQuery map(Record record) {
        Long selfJenjangId = record.get("self_jenjang_id", Long.class);
        Long jenjangId = record.get("jenjang_id", Long.class);
        Long resolvedJenjangId = selfJenjangId != null ? selfJenjangId : jenjangId;

        JenjangPendidikanResponse jenjang = null;
        if (jenjangId != null) {
            jenjang = new JenjangPendidikanResponse(
                    jenjangId,
                    record.get("jenjang_nama", String.class),
                    record.get("jenjang_short_name", String.class),
                    record.get("jenjang_seq", Integer.class),
                    record.get("jenjang_is_statistik", Boolean.class)
            );
        }

        Byte isLatestRaw = record.get("is_latest", Byte.class);
        Boolean isLatest = isLatestRaw != null && isLatestRaw == 1;
        Byte disetujuiRaw = record.get("disetujui", Byte.class);
        Boolean disetujui = disetujuiRaw != null && disetujuiRaw == 1;

        return new PendidikanQuery(
                record.get("id", Long.class),
                record.get("biodata_id", String.class),
                record.get("biodata_nik", String.class),
                record.get("biodata_nama", String.class),
                resolvedJenjangId,
                jenjang,
                record.get("gelar_depan", String.class),
                record.get("gelar_belakang", String.class),
                record.get("jurusan", String.class),
                record.get("institusi", String.class),
                record.get("kota", String.class),
                record.get("tahun_masuk", Integer.class),
                record.get("is_lulus", Boolean.class),
                record.get("tahun_lulus", Integer.class),
                record.get("gpa", Double.class),
                isLatest,
                disetujui,
                record.get("tanggal_pengajuan", LocalDateTime.class),
                record.get("tanggal_disetujui", LocalDateTime.class),
                record.get("disetujui_oleh", String.class),
                record.get("changed_status", Byte.class)
        );
    }
}
