package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanQuery;
import org.jooq.Record;
import org.jooq.RecordMapper;

class PendidikanMultisetMapper implements RecordMapper<Record, PendidikanQuery> {
    @Override
    public PendidikanQuery map(Record record) {
        PendidikanQuery q = new PendidikanQuery();
        q.setId(record.get("id", Long.class));
        q.setBiodataId(record.get("biodata_id", String.class));
        q.setGelarDepan(record.get("gelar_depan", String.class));
        q.setGelarBelakang(record.get("gelar_belakang", String.class));
        q.setJurusan(record.get("jurusan", String.class));
        q.setInstitusi(record.get("institusi", String.class));
        q.setKota(record.get("kota", String.class));
        q.setTahunMasuk(record.get("tahun_masuk", Integer.class));
        q.setIsLulus(record.get("is_lulus", Boolean.class));
        q.setTahunLulus(record.get("tahun_lulus", Integer.class));
        q.setGpa(record.get("gpa", Double.class));
        q.setIsLatest(record.get("is_latest", Boolean.class));
        q.setChangedStatus(record.get("changed_status", Byte.class));
        return q;
    }
}
