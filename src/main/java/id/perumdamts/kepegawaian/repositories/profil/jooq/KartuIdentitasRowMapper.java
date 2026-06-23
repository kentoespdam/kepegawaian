package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasQuery;
import org.jooq.Record;
import org.jooq.RecordMapper;

import java.util.Objects;

public class KartuIdentitasRowMapper implements RecordMapper<Record, KartuIdentitasQuery> {
    @Override
    public KartuIdentitasQuery map(Record record) {
        KartuIdentitasQuery q = new KartuIdentitasQuery();
        q.setId(record.get("id", Long.class));
        q.setBiodataId(record.get("self_nik", String.class));
        q.setBiodataNik(record.get("biodata_nik", String.class));
        q.setBiodataNama(record.get("biodata_nama", String.class));
        q.setJenisKartuId(Objects.requireNonNullElse(record.get("jenis_kartu_id", Long.class), record.get("self_jenis_kitas_id", Long.class)));
        q.setJenisKartuNama(record.get("jenis_kartu_nama", String.class));
        q.setNomorKartu(record.get("nomor_kartu", String.class));
        q.setTanggalExpired(record.get("tanggal_expired", java.time.LocalDate.class));
        q.setTanggalTerima(record.get("tanggal_terima", java.time.LocalDate.class));
        q.setNotes(record.get("notes", String.class));
        q.setChangedStatus(record.get("changed_status", Byte.class));
        return q;
    }
}
