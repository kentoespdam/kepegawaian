package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanQuery;
import org.jooq.Record;
import org.jooq.RecordMapper;

import java.util.Objects;

public class PelatihanRowMapper implements RecordMapper<Record, PelatihanQuery> {
    @Override
    public PelatihanQuery map(Record record) {
        PelatihanQuery q = new PelatihanQuery();
        q.setId(record.get("id", Long.class));
        q.setBiodataId(record.get("biodata_id", String.class));
        q.setBiodataNik(record.get("biodata_nik", String.class));
        q.setBiodataNama(record.get("biodata_nama", String.class));
        Long jenisPelatihanId = record.get("jenis_pelatihan_id", Long.class);
        q.setJenisPelatihanId(Objects.requireNonNullElse(record.get("self_jenis_pelatihan_id", Long.class), jenisPelatihanId));
        q.setJenisPelatihanNama(record.get("jenis_pelatihan_nama", String.class));
        q.setNama(record.get("nama", String.class));
        q.setLembaga(record.get("lembaga", String.class));
        q.setTanggalMulai(record.get("tanggal_mulai", java.time.LocalDate.class));
        q.setTanggalSelesai(record.get("tanggal_selesai", java.time.LocalDate.class));
        q.setLulus(record.get("lulus", Boolean.class));
        q.setNilai(record.get("nilai", String.class));
        q.setIkatanDinas(record.get("ikatan_dinas", Boolean.class));
        q.setTanggalAkhirIkatan(record.get("tanggal_akhir_ikatan", java.time.LocalDate.class));
        q.setNotes(record.get("notes", String.class));
        q.setChangedStatus(record.get("changed_status", Byte.class));
        return q;
    }
}