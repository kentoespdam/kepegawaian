package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanResponse;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanQuery;
import org.jooq.Record;
import org.jooq.RecordMapper;

import java.util.Objects;

public class PendidikanRowMapper implements RecordMapper<Record, PendidikanQuery> {

    @Override
    public PendidikanQuery map(Record record) {
        PendidikanQuery q = new PendidikanQuery();
        q.setId(record.get("id", Long.class));
        q.setBiodataId(record.get("biodata_id", String.class));
        q.setBiodataNik(record.get("biodata_nik", String.class));
        q.setBiodataNama(record.get("biodata_nama", String.class));

        Long jenjangId = record.get("jenjang_id", Long.class);
        q.setJenjangId(Objects.requireNonNullElse(record.get("self_jenjang_id", Long.class), jenjangId));

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
