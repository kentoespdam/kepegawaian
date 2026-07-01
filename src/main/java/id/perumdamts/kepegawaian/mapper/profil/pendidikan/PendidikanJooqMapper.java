package id.perumdamts.kepegawaian.mapper.profil.pendidikan;

import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanResponse;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanQuery;
import org.jooq.Record;
import org.jooq.RecordMapper;

public final class PendidikanJooqMapper implements RecordMapper<Record, PendidikanQuery> {
    public static final PendidikanJooqMapper INSTANCE = new PendidikanJooqMapper();

    private PendidikanJooqMapper() {}

    @Override
    public PendidikanQuery map(Record record) {
        PendidikanQuery q = new PendidikanQuery();
        q.setId(record.get("id", Long.class));
        q.setBiodataId(record.get("biodata_id", String.class));
        q.setBiodataNik(record.get("biodata_nik", String.class));
        q.setBiodataNama(record.get("biodata_nama", String.class));

        // self_jenjang_id is the authoritative FK on the pendidikan row; jenjang_id is the
        // joined master id (present only when the FK resolves). Both null on rows without jenjang.
        Long selfJenjangId = record.get("self_jenjang_id", Long.class);
        Long jenjangId = record.get("jenjang_id", Long.class);
        q.setJenjangId(selfJenjangId != null ? selfJenjangId : jenjangId);

        if (jenjangId != null) {
            JenjangPendidikanResponse jp = new JenjangPendidikanResponse();
            jp.setId(jenjangId);
            jp.setNama(record.get("jenjang_nama", String.class));
            jp.setShortName(record.get("jenjang_short_name", String.class));
            jp.setSeq(record.get("jenjang_seq", Integer.class));
            jp.setIsStatistik(record.get("jenjang_is_statistik", Boolean.class));
            q.setJenjangPendidikan(jp);
        }

        q.setGelarDepan(record.get("gelar_depan", String.class));
        q.setGelarBelakang(record.get("gelar_belakang", String.class));
        q.setJurusan(record.get("jurusan", String.class));
        q.setInstitusi(record.get("institusi", String.class));
        q.setKota(record.get("kota", String.class));
        q.setTahunMasuk(record.get("tahun_masuk", Integer.class));
        q.setIsLulus(record.get("is_lulus", Boolean.class));
        q.setTahunLulus(record.get("tahun_lulus", Integer.class));
        q.setGpa(record.get("gpa", Double.class));

        Byte isLatest = record.get("is_latest", Byte.class);
        q.setIsLatest(isLatest != null && isLatest == 1);

        q.setChangedStatus(record.get("changed_status", Byte.class));
        return q;
    }
}
