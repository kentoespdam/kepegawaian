package id.perumdamts.kepegawaian.mapper.profil.kartuIdentitas;

import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasDetail;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;
import org.jooq.Record;
import org.jooq.RecordMapper;

import java.util.List;

public final class KartuIdentitasDetailJooqMapper implements RecordMapper<Record, KartuIdentitasDetail> {
    public static final KartuIdentitasDetailJooqMapper INSTANCE = new KartuIdentitasDetailJooqMapper();

    private KartuIdentitasDetailJooqMapper() {}

    @SuppressWarnings("unchecked")
    @Override
    public KartuIdentitasDetail map(Record record) {
        KartuIdentitasDetail d = new KartuIdentitasDetail();
        d.setId(record.get("id", Long.class));
        d.setBiodataId(record.get("self_nik", String.class));
        d.setBiodataNik(record.get("biodata_nik", String.class));
        d.setBiodataNama(record.get("biodata_nama", String.class));
        d.setJenisKartuId(record.get("jenis_kartu_id", Long.class));
        d.setJenisKartuNama(record.get("jenis_kartu_nama", String.class));
        d.setNomorKartu(record.get("nomor_kartu", String.class));
        d.setTanggalExpired(record.get("tanggal_expired", java.time.LocalDate.class));
        d.setTanggalTerima(record.get("tanggal_terima", java.time.LocalDate.class));
        d.setNotes(record.get("notes", String.class));
        d.setChangedStatus(record.get("changed_status", Byte.class));
        List<LampiranRow> lampiran = record.get("lampiran", List.class);
        d.setLampiran(lampiran != null ? lampiran : List.of());
        return d;
    }
}
