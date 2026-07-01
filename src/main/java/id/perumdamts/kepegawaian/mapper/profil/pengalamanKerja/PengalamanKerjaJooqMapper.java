package id.perumdamts.kepegawaian.mapper.profil.pengalamanKerja;

import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.PengalamanKerjaQuery;
import org.jooq.Record;
import org.jooq.RecordMapper;

import java.util.Map;

public final class PengalamanKerjaJooqMapper implements RecordMapper<Record, PengalamanKerjaQuery> {
    public static final PengalamanKerjaJooqMapper INSTANCE = new PengalamanKerjaJooqMapper();

    private PengalamanKerjaJooqMapper() {}
    @Override
    public PengalamanKerjaQuery map(Record record) {
        Map<String, Object> map = record.intoMap();
        var query = new PengalamanKerjaQuery();
        query.setId((Long) map.get("id"));
        query.setBiodataId((String) map.get("biodata_id"));
        query.setBiodataNik((String) map.get("biodata_nik"));
        query.setBiodataNama((String) map.get("biodata_nama"));
        query.setNamaPerusahaan((String) map.get("nama_perusahaan"));
        query.setTypePerusahaan((String) map.get("type_perusahaan"));
        query.setJabatan((String) map.get("jabatan"));
        query.setLokasi((String) map.get("lokasi"));
        query.setTahunMasuk((Integer) map.get("tahun_masuk"));
        query.setTahunKeluar((Integer) map.get("tahun_keluar"));
        query.setNotes((String) map.get("notes"));
        Byte cs = (Byte) map.get("changed_status");
        query.setChangedStatus(cs);
        return query;
    }
}
